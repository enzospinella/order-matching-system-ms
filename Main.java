import java.io.*;
public class Main
{
    public static void main(String[] args)
    {
        MatchingEngine engine = new MatchingEngine();
        
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(reader);

        OutputStreamWriter writer = new OutputStreamWriter(System.out);
        BufferedWriter bw = new BufferedWriter(writer);
        String line;
        while(true)
        {
            try
            {
                bw.write(">>> ");
                bw.flush();
                line = br.readLine();
                if (line == null || line.equals("exit"))
                    break;
                
                String[] parts = line.split(" ");
                if (parts.length < 3)
                {
                    bw.write("Invalid command");
                    bw.newLine();
                    bw.flush();
                    continue;
                }
                
                String command = parts[0];
                String side = parts[1];
                
                if (command.equals("limit"))
                {
                    if (parts.length < 4)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    double price = Double.parseDouble(parts[2]);
                    int qty = Integer.parseInt(parts[3]);

                    LimitOrder order = new LimitOrder(side, price, qty);
                    for (Trade trade : engine.processOrder(order).values())
                    {
                        bw.write(trade.toString());
                        bw.newLine();
                        bw.flush();
                    }
                }
                else if (command.equals("market"))
                {
                    if (parts.length < 3)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    int qty = Integer.parseInt(parts[2]);

                    MarketOrder order = new MarketOrder(side, qty);
                    for (Trade trade : engine.processOrder(order).values())
                    {
                        bw.write(trade.toString());
                        bw.newLine();
                        bw.flush();
                    }
                }
                else
                {
                    bw.write("Invalid command");
                    bw.newLine();
                    bw.flush();
                    continue;
                }
                
            }
            catch (Exception e)
            {
                try 
                {
                    bw.write("Error: " + e.getMessage());
                    bw.newLine();
                    bw.flush();
                }
                catch (IOException ex)
                {
                    System.err.println("Error writing to output: " + ex.getMessage());
                }
            }
        }
    }
}