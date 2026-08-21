package helper;

import java.util.Scanner;

public class Utils
{

    public static final Scanner sc = new Scanner(System.in);

    public static void p(Object ob)
    {
        System.out.print(ob);
    }

    public static void pl(Object ob)
    {
        System.out.println(ob);
    }

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
