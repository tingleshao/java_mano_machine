/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manomachine.logic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 *
 * @author Administrator
 */
public class CodeParser {

    private HashSet<String> pseudo = new HashSet<String>();
    private HashMap<String, String> MRI = new HashMap<String, String>();
    private HashMap<String, String> NMRI = new HashMap<String, String>();
    public HashMap<String, String> Labels = new HashMap<String, String>();
    int LC;
    public HashMap<String, String> hexCodes = new HashMap<String, String>();
    private HashMap<Integer, Integer> operands = new HashMap<Integer, Integer>();

    public CodeParser() {
        LC = 0;

        pseudo.add("ORG");
        pseudo.add("END");
        pseudo.add("HEX");
        pseudo.add("DEC");

        MRI.put("AND", "0");
        MRI.put("ADD", "1");
        MRI.put("LDA", "2");
        MRI.put("STA", "3");
        MRI.put("BUN", "4");
        MRI.put("BSA", "5");
        MRI.put("ISZ", "6");
        NMRI.put("CLA", "7800");
        NMRI.put("CLE", "7400");
        NMRI.put("CMA", "7200");
        NMRI.put("CME", "7100");
        NMRI.put("CIR", "7080");
        NMRI.put("CIL", "7040");
        NMRI.put("INC", "7020");
        NMRI.put("SPA", "7010");
        NMRI.put("SNA", "7008");
        NMRI.put("SZA", "7004");
        NMRI.put("SZE", "7002");
        NMRI.put("HLT", "7001");
        NMRI.put("INP", "F800");
        NMRI.put("OUT", "F400");
        NMRI.put("SKI", "F200");
        NMRI.put("SKO", "F100");
        NMRI.put("ION", "F080");
        NMRI.put("IOF", "F040");

    }

    public void firstPass(String s) {



        String[] text = s.split("\n");

        for (String line : text) {
            if (line.contains("ORG")) {
                LC = Integer.parseInt(line.substring(4, line.length()), 16);
                continue;

            }
            if (line.contains("END")) {
                break;
            }


            String[] elements = line.split(" ");
            for (String element : elements) {
                if (pseudo.contains(element)) {
                    continue;
                }


                if (element.contains(",")) {
                    Labels.put(element.substring(0, element.length() - 1), Integer.toHexString(LC));
                    continue;
                }


            }
            LC++;
        }


    }

    public String trimLabels(HashMap<String,String> labels) {
       
        String s = labels.toString();

        String text1 = s.substring(1, s.length() - 1);
        String text2 = text1.replaceAll(",", "\n");
        System.out.println("text2: " + text2);     //test
        String[] text3 = text2.split("\n");
        String[] text4 = new String[text3.length];
        String text5 = new String(" ");
        int n =0;
        System.out.print("text3: ");     //test
        for(String t : text3)           //test
            System.out.println(t);      //test
        for(String t : text3)
        {text4[text3.length-1-n]= t;
         n++;
        }
        System.out.println("text4: ");// test
        for(String t : text4)        //test
            System.out.println(t);  //test
        for(String t : text4)
        {
         text5 = text5.concat(t).concat("\n");
        }

         text5=   text5.replaceAll(" ", "");
             
      System.out.println(text5);  //test
        return text5;



    }

    public void secondPass(String s) {
        LC = 0;
        String[] text = s.split("\n");

        for (String line : text) {
            String[] elements = line.split(" ");
            LinkedList<String> elementsList = new LinkedList<String>();
            for (String element : elements) {
                elementsList.add(element);
            }

            Iterator it = elementsList.iterator();

            if (elementsList.getFirst().equals("END")) {
                break;
            }

            if (elementsList.getFirst().equals("ORG")) {
                try {
                    LC = Integer.parseInt(elementsList.get(1), 16);
                    continue;

                } catch (NumberFormatException nfe) {
                    //NumberFormatException
                } catch (Exception e) {
                    // number does not exist
                }
            }

            if (Labels.containsKey(elementsList.getFirst().substring(0, elementsList.getFirst().length()-1))) {
                try {
                    if (elementsList.get(1).equals("HEX")) {
                        hexCodes.put(Integer.toHexString(LC), fillZero(elementsList.get(2)));
                        
                        LC++;
                        continue;
                    }

                    else if (elementsList.get(1).equals("DEC")) {
                        hexCodes.put(Integer.toHexString(LC), fillZero(Integer.toHexString(Integer.parseInt(elementsList.get(2)))));
                        LC++;
                        continue;
                    }
                    else {
                    System.out.println("Syntax error");}
                } catch (NumberFormatException nfe) {
                    System.out.println("invalid number format to operands");
                } catch (Exception e) {

                   System.out.println("other exception in operands.");
                }


            }


            if (MRI.containsKey(elementsList.get(0))) //  is a MRI
            {
                if (Labels.containsKey(elementsList.get(1))) {
                    Integer number = Integer.parseInt(MRI.get(elementsList.get(0)));
                   try{ if (elementsList.get(2).equals("I")) {
                        number += 8;
                    }

                   }catch(Exception e)
                   {
                    System.out.println("not INDIRECT INSTRUCTION");

                   }
                    String s1 = Integer.toHexString(number).concat(Labels.get(elementsList.get(1)));
                    hexCodes.put(Integer.toHexString(LC), s1);
                }

                LC++;
                continue;
            }

            if (NMRI.containsKey(elementsList.get(0))) {
                hexCodes.put(Integer.toHexString(LC), NMRI.get(elementsList.get(0)));
                LC++;
                continue;
            }

        }

    }

    public String fillZero(String s)
    {
         String x = new String("");

        for(int i = 0 ; i < 4-s.length(); i++)
        {
              x= x.concat("0");

        }


        x= x.concat(s);
        return x;
    }
    public static void main(String args[]) {
        CodeParser cp = new CodeParser();
        Scanner kb = new Scanner(System.in);
        String input = kb.nextLine();
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        input = input.concat("\n").concat(kb.nextLine());
        cp.firstPass(input);
        cp.secondPass(input);
        System.out.println(cp.hexCodes);



    }
}
