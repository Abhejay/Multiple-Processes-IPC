import java.util.*;
import java.io.*;


public class Cpu {
    //Registers
    private int PC, IR, AC, X, Y = 0;
    private int SP = 1000;


    private int timer = 0;
    private int timeOut;

    //Kernel Mode
    private boolean kMode = false;

    //Input and Output Memory for the memory
    private Scanner inputMemory;
    private PrintWriter outputMemory;

    //Constructor for the Cpu class
    public Cpu(Scanner inMem, PrintWriter outMem, int tOut) {
        inputMemory = inMem;
        outputMemory = outMem;
        timeOut = tOut;
    }

    //FETCH the instructions from Memory into IR Register
    void search() { IR = readMem(PC++); }

    //PUSH data to register stack, call writeMem
    void push(int data) { writeMem(--SP, data); }

    //POP data to register stack, return readMem with SP increment
    int pop() { return readMem(SP++); }

    //READ memory method which takes in address as parameter to check the kernel mode of system
    int readMem(int addr) {
        if (addr >= 1000 && !kMode) {
            System.out.println("ERROR: Accessed system memory in user mode.");
            System.exit(-1);
        }
        outputMemory.println("r" + addr);
        outputMemory.flush();
        return Integer.parseInt(inputMemory.nextLine());
    }

    //Memory address and data inside output
    void writeMem(int addr, int data) {
        outputMemory.println("w" + addr + "," + data);
        outputMemory.flush();
    }

    //Ends memory process
    void endMem() {
        outputMemory.println("e");
        outputMemory.flush();
    }

    //Saves user mode and enters kernel mode
    void modeOfKernel() {
        kMode = true;
        int temporarySP = SP;
        SP = 2000;
        push(temporarySP);
        push(PC);
        push(IR);
        push(AC);
        push(X);
        push(Y);
    }

    //Fetch and execution cycles until end
    void run() {
        boolean runner = true;
        while (runner) {
            search();
            runner = execute();
            timer++;

            if (timer >=  timeOut) {
                if (!kMode) {
                    timer = 0;
                    modeOfKernel();
                    PC = 1000;
                }
            }
        }
    }

    //Executes the instructions (Returns true if running and false if not)
    //Based on Instruction list provided in the Project manual
    boolean execute()
    {
        switch(IR)
        {
            case 1:
                search();
                AC = IR;
                break;
            case 2:
                search();
                AC = readMem(IR);
                break;
            case 3:
                search();
                AC = readMem(readMem(IR));
                break;
            case 4:
                search();
                AC = readMem(IR + X);
                break;
            case 5:
                search();
                AC = readMem(IR + Y);
                break;
            case 6:
                AC = readMem(SP + X);
                break;
            case 7:
                search();
                writeMem(IR, AC);
                break;
            case 8:
                AC = (int)(Math.random() * 100 + 1);
                break;
            case 9:
                search();
                if (IR == 1)
                    System.out.print(AC);
                else if (IR == 2)
                    System.out.print((char)AC);

                break;
            case 10:
                AC += X;
                break;
            case 11:
                AC += Y;
                break;
            case 12:
                AC -= X;
                break;
            case 13:
                AC -= Y;
                break;
            case 14:
                X = AC;
                break;
            case 15:
                AC = X;
                break;
            case 16:
                Y = AC;
                break;
            case 17:
                AC = Y;
                break;
            case 18:
                SP = AC;
                break;
            case 19:
                AC = SP;
                break;
            case 20:
                search();
                PC = IR;
                break;
            case 21:
                search();
                if (AC == 0)
                    PC = IR;

                break;
            case 22:
                search();
                if (AC != 0)
                    PC = IR;

                break;
            case 23:
                search();
                push(PC);
                PC = IR;
                break;
            case 24:
                PC = pop();
                break;
            case 25:
                X++;
                break;
            case 26:
                X--;
                break;
            case 27:
                push(AC);
                break;
            case 28:
                AC = pop();
                break;
            case 29:
                if (!kMode) {
                    modeOfKernel();
                    PC = 1500;
                }

                break;
            case 30:
                Y = pop();
                X = pop();
                AC = pop();
                IR = pop();
                PC = pop();
                SP = pop();

                kMode = false;
                break;
            case 50:
                endMem();
                return false;
            default:
                System.err.println("Instruction is invalid");
                endMem();
                return false;
        }

        return true;
    }

    //Main Function
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("There are not enough arguments.");
            System.exit(1);
        }

        //The Program Arguments
        String input = args[0];
        int tOut = Integer.parseInt(args[1]);
        Runtime duration = Runtime.getRuntime();


        //Calling the Memory process with the program argument
        try {
            Process m = duration.exec("java Memory " + input);
            final InputStream error = m.getErrorStream();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffr = new byte[8192];
                    int len = -1;
                    try {
                        while((len = error.read(buffr)) > 0) {
                            System.err.write(buffr, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //Read and output Memory
            Scanner inputMemory = new Scanner(m.getInputStream());
            PrintWriter outputMemory = new PrintWriter(m.getOutputStream());

            //Instance of Cpu class for execution
            Cpu cpu = new Cpu(inputMemory, outputMemory, tOut);
            cpu.run();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Not able to create new process");
            System.exit(1);
        }
    }
}
