import java.util.*;
import java.io.*;


public class Memory {

    //Integer array that is memory
    static int[] mem;

    //WRITE method
    static void write(int addr, int data) { mem[addr] = data; }

    //READ method
    static int read(int addr) { return mem[addr]; }


    //Allocates the memory & loads program from the txt file
    static void initMem(String inFilePath) throws FileNotFoundException {
        mem = new int[2000];

        Scanner in =  new Scanner(new File(inFilePath));
        int memIndex = 0;
        while (in.hasNextLine()) {
            String ln = in.nextLine().trim();

            if (ln.length() < 1) //Empty line
                continue;

            if (ln.charAt(0) == '.') { //Move position in memory
                memIndex = Integer.parseInt(ln.substring(1).split("\\s+")[0]);
                continue;
            }

            if (ln.charAt(0) < '0' || ln.charAt(0) > '9') { //First part of line skip
                continue;
            }

            //Whitespace split
            String[] splt = ln.split("\\s+");

            if (splt.length < 1)
                continue;
            else
                mem[memIndex++] = Integer.parseInt(splt[0]);


        }

        in.close();
    }

    //Main function for execution
    public static void main(String[] args) throws FileNotFoundException {

        //If there is no input
        if (args.length < 1) {
            System.err.println("Not enough arguments are passed");
            System.exit(1);
        }
        else {
            String inPath = args[0];
            initMem(inPath);

            Scanner in = new Scanner(System.in);

            while(in.hasNextLine()) {

                String ln = in.nextLine();
                char cmd = ln.charAt(0);
                int value;
                int address;

                if (cmd == 'r') { //READ command
                    address = Integer.valueOf(ln.substring(1));
                    System.out.println(read(address));
                }
                else if (cmd == 'w') { //WRITE command
                    String[] param = ln.substring(1).split(",");
                    address = Integer.valueOf(param[0]);
                    value = Integer.valueOf(param[1]);

                    write(address, value);
                }
                else if (cmd == 'e') { //END command
                    System.exit(0);
                }
            }

            in.close();
        }
    }


}
