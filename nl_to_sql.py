import re
import sqlite3
import ollama

MODEL   = "phi3"
DB_FILE = "inventory.db"

# ── Schema ────────────────────────────────────────────────────────────────────

def get_schema() -> str:
    conn = sqlite3.connect(DB_FILE)
    cur  = conn.cursor()
    cur.execute("SELECT sql FROM sqlite_master WHERE type='table' ORDER BY name")
    rows = cur.fetchall()
    conn.close()
    return "\n\n".join(r[0] for r in rows if r[0])

# ── NL → SQL via Ollama ───────────────────────────────────────────────────────

def nl_to_sql(question: str, schema: str) -> str:
    """
    Prompt engineering:
    - System role sets strict SQL-only behaviour.
    - User message embeds schema + question inline so the model
      has full context in one turn (reduces hallucination).
    - temperature=0 for deterministic, reproducible SQL.
    - max_tokens capped to avoid runaway completions.
    - Only SELECT queries are requested; the safety guard in run_sql
      enforces this as a second line of defence.
    """
    system_msg = (
        "You are an expert SQLite query generator for an inventory management system.\n"
        "Rules:\n"
        "  1. Output ONLY the raw SQL SELECT statement — no markdown, no explanation, no code fences.\n"
        "  2. Only generate SELECT queries. Never INSERT, UPDATE, DELETE, or DROP.\n"
        "  3. Use only the tables and columns provided in the schema.\n"
        "  4. If the request cannot be expressed as a SELECT query, reply with exactly: CANNOT_GENERATE"
    )

    user_msg = (
        f"Schema:\n{schema}\n\n"
        f"Question: {question}\n\n"
        "Return only SQL."
    )

    response = ollama.chat(
        model    = MODEL,
        messages = [
            {"role": "system", "content": system_msg},
            {"role": "user",   "content": user_msg},
        ],
        options  = {"temperature": 0, "num_predict": 200},
    )
    raw     = response["message"]["content"]
    cleaned = re.sub(r"```(?:sql)?(.*?)```", r"\1", raw, flags=re.DOTALL | re.IGNORECASE)
    return cleaned.strip()

# ── Execute SQL (SELECT-only safety guard) ────────────────────────────────────

def run_sql(sql: str) -> list[tuple]:
    """Block any non-SELECT statement before it touches the database."""
    if not sql.strip().upper().startswith("SELECT"):
        raise ValueError(f"Unsafe SQL blocked: {sql!r}")

    conn = sqlite3.connect(DB_FILE)
    cur  = conn.cursor()
    try:
        cur.execute(sql)
        return cur.fetchall()
    finally:
        conn.close()

# ── Explainability: second LLM call ──────────────────────────────────────────

def explain_result(question: str, sql: str, rows: list[tuple], all_data: list[tuple]) -> str:
    """
    Prompt engineering for explainability:
    - Passes the full inventory snapshot (all_data) so the model can
      contextualise results (e.g. "low stock compared to everything else").
    - Includes the generated SQL so the model can reference what was queried.
    - 'Do NOT invent reasons' instruction prevents hallucination.
    - temperature=0.2 allows slightly more natural language while staying grounded.
    """
    system_msg = (
        "You explain SQLite query results clearly and concisely for a non-technical user.\n"
        "Rules:\n"
        "  1. Explain ONLY using the data provided — do NOT invent reasons or assumptions.\n"
        "  2. Reference specific values from the results when possible.\n"
        "  3. Keep the explanation under 4 sentences."
    )

    user_msg = (
        f"Table columns: name, itemid, quantity, price\n\n"
        f"Business rule: an item may need reordering if its quantity is low relative to others.\n\n"
        f"Full inventory snapshot:\n{all_data}\n\n"
        f"User question: {question}\n\n"
        f"SQL used: {sql}\n\n"
        f"Query result rows: {rows}\n\n"
        "Explain the result to the user."
    )

    response = ollama.chat(
        model    = MODEL,
        messages = [
            {"role": "system", "content": system_msg},
            {"role": "user",   "content": user_msg},
        ],
        options  = {"temperature": 0.2, "num_predict": 150},
    )
    return response["message"]["content"].strip()

# ── Main ──────────────────────────────────────────────────────────────────────

def handle_question(question: str, schema: str) -> None:
    """Process a single NL question: generate SQL, run it, explain it."""
    sql = nl_to_sql(question, schema)

    if sql == "CANNOT_GENERATE":
        print("Could not generate SQL for that request.")
    else:
        print(f"\nSQL: {sql}\n")
        try:
            results  = run_sql(sql)
            all_data = run_sql("SELECT * FROM inventory")

            print("Result:")
            for row in results:
                print(" ", row)

            explanation = explain_result(question, sql, results, all_data)
            print(f"\nExplanation:\n{explanation}")

        except ValueError as e:
            print(f"Blocked: {e}")
        except Exception as e:
            print(f"Error: {e}")


if __name__ == "__main__":
    import sys

    schema = get_schema()

    # --repl mode: spawned by Java via ProcessBuilder.
    # Reads questions line-by-line from stdin; exits on "quit".
    # stdout is flushed after every response so the Java reader never blocks.
    if "--repl" in sys.argv:
        print("nl_to_sql ready. Send questions via stdin (type 'quit' to exit).", flush=True)
        for line in sys.stdin:
            question = line.strip()
            if not question:
                continue
            if question.lower() == "quit":
                print("Exiting nl_to_sql.", flush=True)
                break
            handle_question(question, schema)
            # Flush so Java's output drainer thread receives the full response
            sys.stdout.flush()

    # Interactive single-question mode (direct use from terminal)
    else:
        question = input("Ask a question about inventory: ").strip()
        handle_question(question, schema)
