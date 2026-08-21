package base;

import static helper.Utils.*;
import java.io.IOException;
import java.nio.file.Path;

public class Main
{
    public static void main(String args[])
    {
        Inbox inbox = new Inbox(Path.of("vault", "inbox"));

        pl("Inbox Folder Set at Path:vault/inbox");
        boolean runner=true;
        while (runner)
        {
            String menu = """

                    1-Enter a Thought
                    2-List the current Thoughts
                    3-Exit!

                    <Enter Choice>!!
                    """;

            pl(menu);
            int choice = inpint();

            switch (choice)
            {
            case 1:
                pl("Enter Thought in Plain Text (press enter to save):");
                String text = sc.nextLine();
                try
                {
                    Path p = inbox.add(text);
                    pl("Saved the thought to:" + p.toString());
                }
                catch (IOException e)
                {
                    pl("Error Occured in saving the thought....!!!");
                    pl(e.getMessage());
                    pl("Please renter your thought , in case of repetitive errors , check logs!!");
                    pl("Your entered thought:");
                    pl(text);
                }
                break;
            case 2:
                try
                {

                    String list = inbox.list();
                    if (list.equals( ""))
                        pl("Inbox is Currently Empty!");
                    else
                    {
                        pl("Current List:");
                        pl(list);
                    }
                }
                catch (IOException e)
                {
                    pl("Error Displaying the List....!");
                    pl(e.getMessage());
                    pl("In case of repetitive errors , check logs!!");
                }
                break;
            case 3:
                pl("You want to exit ? Press 3 once again to confirm ");
                int exittt = inpint();
                if (exittt == 3)
                {
                    pl("Exiting , goodbye :) !");
                    runner = false;
                }

                break;
            default:
                pl("Option Not found! , Press 3 to exit !");
            }
        }
    }

}