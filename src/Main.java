import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Main {

    private static ArrayList<String> lexArray = new ArrayList<>();
    private static String lexStr = "";
    static String filename;

    public static void main(String[] args) {
        System.out.println(new File("lexical.c").getAbsolutePath());

        filename = "lexical.c";
        File file = new File(filename);

        try (InputStream in = new FileInputStream(file);
            Reader reader = new InputStreamReader(in, Charset.defaultCharset());
            // buffer for efficiency
            Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleCharacters(Reader reader)
            throws IOException {
        int r, lCount = 0;
        StringBuilder buf = new StringBuilder();
        while ((r = reader.read()) != -1) {
            char ch = (char) r;
            if(ch == '\n'){
                buf.delete(0,buf.capacity());
                continue;
            }

            //if identifier of keyword
            //if char is not space or newline
            if(ch != ' '){
                //check if value is operator
                String out;
                if((out = checkOperator(ch)) != null){
                    //add operator to output
                    lexStr += out;

                    handleBuffer(buf);
                    //empty buffer
                    buf.delete(0,buf.capacity());
                }else{
                    //if char is not an operator add it to buffer
                    buf.append(ch);
                    System.out.println(buf);
                    //handle Strings
                    if(ch == '"'){
                        StringBuilder sbStr = new StringBuilder();
                        sbStr.append(ch);
                        while ((r = reader.read()) != -1) {
                            char s_ch = (char) r;
                            if(s_ch != '"'){
                                sbStr.append(s_ch);
                            }else {
                                sbStr.append(s_ch);
                                //end of string, exit
                                //System.out.println(sbStr);
                                buf.delete(0,buf.capacity());
                                lexStr += ("<"+sbStr+">");
                                break;
                            }
                        }
                        System.out.println("<STRING> - >"+sbStr);
                    }
                }
            }else {

                //if space of other new line

                handleBuffer(buf);
                //empty string
                buf.delete(0,buf.capacity());
            }
        }
        System.out.println(lexArray.toString());

        System.out.println(lexStr);

        printToFile(filename,lexStr);
    }

    private static void handleBuffer(StringBuilder buf){
        //remove spaces from buffer
        buf = new StringBuilder(buf.toString()
                .trim()
                .replace(" ","")
                .replace("\n",""));
        if(buf.toString().equals("") || buf.toString().equals(" ")){
            return;
        }

        //check if string in buffer is keyword or is in lex array
        String out = "";
        if ((out = checkKeyword(buf.toString())) != null){
            //is keyword, add to lex array and String
            lexStr += out;
            Character[] lexArr = buf.chars().mapToObj(c -> (char) c).toArray(Character[]::new);
            //lexArray.addAll(Arrays.asList(lexArr));
            if(!lexContains(buf.toString())){
                System.out.println("mKeyword-> "+Arrays.toString(lexArr).trim()
                    .replace("[","")
                    .replace("]",""));
                lexArray.addAll(Collections.singletonList(Arrays.toString(lexArr).trim()
                        .replace("[","")
                        .replace("]","")));
                //check if keyword is in lexArray
                lexArray.add("\\s");
            }
        }else {
            if(isNumeric(buf.toString())){
                //if integer
                System.out.println("INTEGER ->"+buf);
                lexStr += ("<"+buf+">");
            }else if ((out = checkIdentifier(buf.toString())) != null){
                //check if identifier, and add if not in array
                System.out.println("identifier ->"+buf);
                lexStr += out;
            }
                    /*else {
                        System.out.println("variable ->"+buf);
                        lexStr += buf;
                    }*/
        }
    }

    private static String checkOperator(char c){
        String name = null;
        switch (c){
            case '#':
                name = "OP_HASH";
                break;
            case ',':
                name = "OP_COMMA";
                break;
            case ';':
                name = "OP_S_COLON";
                break;
            case '=':
                name = "OP_EQUALS";
                break;
            case '(':
                name = "OP_BR_OPEN";
                break;
            case ')':
                name = "OP_BR_CLOSE";
                break;
            case '{':
                name = "OP_L_BRACE";
                break;
            case '}':
                name = "OP_R_BRACE";
                break;
            case '<':
                name = "OP_LESS";
                break;
            case '>':
                name = "OP_GREATER";
                break;
            case '+':
                name = "OP_SUM";
                break;
            default:
                return null;
        }
        return "<"+name+">";
    }

    private static String checkKeyword(String c){
        switch (c){
            case "int":
                break;
            case "char":
                break;
            case "float":
                break;
            case "include":
                break;
            case "return":
                break;
            default:
                return null;
        }
        return "<"+c+">";
    }

    private static String checkIdentifier(String s){
        s = s.trim().replace(" ","")
                    .replace("\n","");
        String lexString = lexArray.toString()
                .replace(",","")
                .replace("\n","")
                .replace(" ","");
        //System.out.println("lexStr-> "+lexString+"; has -> "+s);
        int pos = 0;
        if(lexContains(s)){
            pos = lexString.indexOf(s) - 1;
            System.out.println("Found at ->"+pos);
        }else {
            addIdentifier(s);
            return checkIdentifier(s);
        }
        return "<ID,"+pos+">";
    }
    private static void addIdentifier(String s){
        System.out.println("add ID -> "+s);
        for (Character ch:s.toCharArray()) {
            //System.out.println(ch);
            lexArray.add(ch.toString());
        }
        lexArray.add("\\s");
    }

    private static boolean lexContains(String s){
        String lexString = lexArray.toString()
                .replace(",","")
                .replace("\n","")
                .replace(" ","");
        System.out.println("lexStr-> "+lexString+"; has -> "+s);
        return lexString.contains(s);
    }
    private static boolean isNumeric(String str){
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static void printToFile(String filename,String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {

            fw = new FileWriter(Main.filename+".lex");
            bw = new BufferedWriter(fw);
            bw.write(content);

            System.out.println("Done");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }


}
