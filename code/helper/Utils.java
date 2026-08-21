package helper;

import java.util.Scanner;

/**
 * Shared console I/O helpers, intended to be used via a static import so call
 * sites stay short.
 */
public class Utils
{

    /**
     * One shared Scanner over System.in. A single instance is required — two
     * Scanners on the same stream would each buffer input and steal lines from
     * one another.
     */
    public static final Scanner sc = new Scanner(System.in);

    /**
     * Prints without a trailing newline.
     *
     * @param ob the value to print
     */
    public static void p(Object ob)
    {
        System.out.print(ob);
    }

    /**
     * Prints followed by a newline.
     *
     * @param ob the value to print
     */
    public static void pl(Object ob)
    {
        System.out.println(ob);
    }

    /**
     * Reads whole lines until one parses as an int, re-prompting on anything
     * else. Reading by line (rather than by token) means a bad entry is consumed
     * entirely instead of being left in the buffer for the next read.
     *
     * @return the entered integer
     */
    public static int inpint()
    {
        while (true)
        {
            try
            {
                return Integer.parseInt(sc.nextLine().trim());
            }
            catch (NumberFormatException e)
            {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    /**
     * Reads whole lines until one parses as a double, re-prompting on anything
     * else.
     *
     * @return the entered double
     */
    public static double inpdob()
    {
        while (true)
        {
            try
            {
                return Double.parseDouble(sc.nextLine().trim());
            }
            catch (NumberFormatException e)
            {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

}
