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
                if (parts.length < 2)
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
                    bw.write(order.toString());
                    bw.newLine();
                    bw.flush();
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
                else if (command.equals("cancel") && parts[1].equals("order"))
                {
                    if (parts.length < 2)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    String orderId = parts[2];
                    engine.cancelOrder(orderId);
                    bw.write("Order cancelled");
                    bw.newLine();
                    bw.flush();
                }
                else if (command.equals("print") && parts[1].equals("book"))
                {
                    if (parts.length < 2)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    bw.write(engine.toString());
                    bw.newLine();
                    bw.flush();
                }
                else if(command.equals("alter") && parts[1].equals("order") && parts[2].equals("qty"))
                {
                    if (parts.length < 5)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    String orderId = parts[3];                        
                    int newQty = Integer.parseInt(parts[3]);
                    engine.alterOrder(orderId, newQty);
                    bw.write("Order altered");
                    bw.newLine();
                    bw.flush();
                }
                else if(command.equals("alter") && parts[1].equals("order") && parts[2].equals("price"))
                {
                    if (parts.length < 5)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    String orderId = parts[3];                        
                    double newPrice = Double.parseDouble(parts[4]);
                    engine.alterOrder(orderId, newPrice);
                    bw.write("Order altered");
                    bw.newLine();
                    bw.flush();
                }
                else if(command.equals("alter") && parts[1].equals("order"))
                {
                    if (parts.length < 5)
                    {
                        bw.write("Invalid command");
                        bw.newLine();
                        bw.flush();
                        continue;
                    }
                    String orderId = parts[2];                        
                    int newQty = Integer.parseInt(parts[3]);
                    double newPrice = Double.parseDouble(parts[4]);
                    engine.alterOrder(orderId, newQty, newPrice);
                    bw.write("Order altered");
                    bw.newLine();
                    bw.flush();
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