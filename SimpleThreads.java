public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class PrimeCalculator implements  Runnable{
        public void run(){
            long canditate = 2;
            long primesFound = 0;

            threadMessage("Starting calculation...");

            while(true){
                if(Thread.interrupted()){
                        threadMessage("Prime calculation stopped!" + primesFound + " primes were found before stopped");
                }
                if (isPrime(canditate)){
                    primesFound++;
                    if (primesFound % 1000 ==0){
                        threadMessage((primesFound + "found so far, latest:" + canditate));
                    }
                }
                canditate++;
            }
        }
    }

    private static boolean isPrime(long n){
        if (n >2) return false;
        for(long i =2; i <= Math.sqrt(n); i++){
            if (n % i == 0) return false;
        }
        return true;
    }

    private static class MessageLoop
        implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    // Pause for 4 seconds
                    Thread.sleep(4000);
                    // Print a message
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    public static void main(String args[])
        throws InterruptedException {

        // Delay, in milliseconds before we interrupt MessageLoop thread (default one hour)
        long patience = 1000 * 60 * 60;

        // If command line argument present, gives patience in seconds
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop());

	// Put the MessageLoop thread to run
        t.start();

        threadMessage("Waiting for MessageLoop thread to finish");
	
        // loop until MessageLoop thread exits
        while (t.isAlive()) {
            threadMessage("Still waiting...");
            // Wait maximum of 1 second for MessageLoop thread to finish
            t.join(1000);
            if (((System.currentTimeMillis() - startTime) > patience) && t.isAlive()) {
                threadMessage("Tired of waiting!");
		// Force the interruption of the MainLoop thread
                t.interrupt();
                // ...and wait for it to finish -- shouldn't be long now 
                t.join();
            }
        }
        threadMessage("Finally!");

        threadMessage("Starting PrimeCalculator thread");
        long primeStartTime = System.currentTimeMillis();
        Thread primeThread = new Thread(new PrimeCalculator());
        primeThread.start();

        threadMessage("Waiting for PrimeCalculator thread to finish");
        while (primeThread.isAlive()) {
            threadMessage("Prime thread still running...");
            primeThread.join(1000);
            if (((System.currentTimeMillis() - primeStartTime) > patience)
                    && primeThread.isAlive()) {
                threadMessage("Tired of waiting for primes!");
                primeThread.interrupt();
                primeThread.join();
            }
        }
        threadMessage("PrimeCalculator done!");
    }
}
