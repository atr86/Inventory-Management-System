import java.io.*;

class Main {
    private static final String syscall = "py";

    public static void main(String[] args) {
        try {
            int n;
            int k = 0;
            receipt r;
            contract con;
            db d = new db();
            // d.extractFull();
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Welcome to Invertory Management System");
            System.out.println("Name of the inventory - NewIndia Inventory");
            System.out.println();
            while (true) {
                System.out.println(
                        "Enter 1 if you are a Customer, 2 if you are a Seller, 3 if you want to see the Inventory state, 4 to Investigate DB with AI, 5 to exit");
                n = Integer.parseInt(br.readLine());
                switch (n) {
                    case 1:
                        System.out.println("Welcome Customer to Inventory");
                        System.out.println("Please enter the following details to get started");
                        customer c = new customer();
                        c.input();
                        r = new receipt(c);
                        r.header();
                        buy b;
                        k = 0;
                        while (true) {
                            b = new buy();
                            b.run();
                            if (b.itemid != -9999 && b.status == true)
                                r.purchase(b);
                            System.out.println("Enter YES if you want More purchases else NO");
                            String ch = br.readLine();
                            if ((ch).compareToIgnoreCase("NO") == 0)
                                break;
                            k++;
                        }
                        r.trailer();
                        break;
                    case 2:
                        System.out.println("Welcome Seller to Inventory");
                        System.out.println("Please enter the following details to get started");
                        seller selr = new seller();
                        selr.input();
                        con = new contract(selr);
                        con.header();
                        sell s;
                        k = 0;
                        while (true) {
                            s = new sell();
                            s.run();
                            if (s.status) {
                                con.purchase(s);
                            }
                            System.out.println("Enter YES if you want to sell more items else NO");
                            String ch = br.readLine();
                            if ((ch).compareToIgnoreCase("NO") == 0)
                                break;
                            k++;
                        }
                        con.trailer();
                        break;
                    case 3:
                        // d.update();
                        d.display();
                        break;
                    case 4:
                        System.out.println("AI Inventory Investigator — type your question, or 'quit' to return.");
                        runNlToSqlSession(br);
                        break;
                    default:
                        break;
                }
                if (n >= 5) {
                    db.saveInCsv();
                    break;
                }

            }
            // d.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts nl_to_sql.py as a subprocess and keeps a conversation loop alive.
     * Each question is written to the process stdin; stdout/stderr are printed
     * live.
     * The loop ends when the user types "quit" (case-insensitive).
     */
    private static void runNlToSqlSession(BufferedReader br) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(syscall, "nl_to_sql.py", "--repl");
        pb.directory(new java.io.File("."));
        pb.redirectErrorStream(true); // merge stderr into stdout
        Process process = pb.start();

        // Writer to send questions into the subprocess stdin
        BufferedWriter processIn = new BufferedWriter(
                new OutputStreamWriter(process.getOutputStream()));

        // Reader to receive the subprocess output
        BufferedReader processOut = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        // Drain subprocess output in a background thread so it never blocks
        Thread outputDrainer = new Thread(() -> {
            try {
                String line;
                while ((line = processOut.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException ignored) {
            }
        });
        outputDrainer.setDaemon(true);
        outputDrainer.start();

        // Main question-answer loop
        while (true) {
            System.out.print("\nYour question (or 'quit'): ");
            String question = br.readLine();
            if (question == null || question.trim().equalsIgnoreCase("quit")) {
                processIn.write("quit\n");
                processIn.flush();
                break;
            }
            processIn.write(question + "\n");
            processIn.flush();
            // Small pause so the drainer thread can print the response
            // before the next prompt appears
            Thread.sleep(4000);
        }

        processIn.close();
        process.waitFor();
        System.out.println("\nAI Investigator session ended.");
    }
}
