import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    public static ArrayList<Token> token_list;
    public static int counter = 0;
    public static Stack<Token> the_stack_tm = new Stack<Token>();
    public void parse_code(String file_name){
        //Grab the token list from the lexer
        Lexer lex = new Lexer();
        lex.lex_text(file_name);
        token_list = lex.tokens;
        the_stack_tm = make_stack();
        //print out stack for debugging purposes
        
       /* System.out.println("\nThe Stack:");
        for(Token t: the_stack_tm){
            System.out.println(t.type + " " + t.value);
        }*/
    }
    //create the stack for the interpreter
    public static Stack<Token> make_stack(){
        token_list = reverse_list(token_list);
        for(int i = 0; i < token_list.size(); i++){
            switch(token_list.get(i).type){
                //add operators and their identifiers on to the stack
                case EQUAL:
                    operator_stack(i);
                    break;
                case PLUS:
                    operator_stack(i);
                    break;
                case MINUS:
                    operator_stack(i);
                    break;
                case MULTIPLY:
                    operator_stack(i);
                    break;
                case DIVIDE:
                    operator_stack(i);
                    break;
                //add function call to the stack
                case FUNCTION_CALL:
                    the_stack_tm.push(token_list.get(i - 3));
                    the_stack_tm.push(token_list.get(i - 2));
                    the_stack_tm.push(token_list.get(i - 1));
                    the_stack_tm.push(token_list.get(i));
                    break;

                //TODO: adding count to stack twice for some reason 
                case ARRAY_FUNCTION:
                    the_stack_tm.push(token_list.get(i - 3));
                    the_stack_tm.push(token_list.get(i - 2));
                    the_stack_tm.push(token_list.get(i - 1));
                    the_stack_tm.push(token_list.get(i));
                    break;
                    
                case BAG_OF_HOLDING:
                    the_stack_tm.push(token_list.get(i - 1));
                    the_stack_tm.push(token_list.get(i));
                    break;
              
                case TILDE:
                    the_stack_tm.push(token_list.get(i));
                    break;
                case WOW:
                    the_stack_tm.push(token_list.get(i - 1));
                    the_stack_tm.push(token_list.get(i));
                    break;
                case EQUALSEQUALS:
                    the_stack_tm.push(token_list.get(i - 1));
                    the_stack_tm.push(token_list.get(i + 1));
                    the_stack_tm.push(token_list.get(i));
                    break;
                case LEFTANGLEBRACE:
                    the_stack_tm.push(token_list.get(i));
                    break;
                case RIGHTANGLEBRACE:
                    the_stack_tm.push(token_list.get(i));
                    break;
                case CONS:
                    the_stack_tm.push(token_list.get(i));
                    break;
                case BOOLEAN:
                    the_stack_tm.push(token_list.get(i));
                    break;

            }
        }
        return the_stack_tm;
    }
    //prepare the list to be made into a stack
    public static ArrayList<Token> reverse_list(ArrayList<Token> token_list){
        ArrayList<Token> temp = new ArrayList<Token>();
        for(int i = token_list.size()-1; i >= 0; i--){
            temp.add(token_list.get(i));
        }
        return temp;
    }
    //function that pushes operator and it's identifiers onto the stat
    public static void operator_stack(int i){
        the_stack_tm.push(token_list.get(i - 1));
        the_stack_tm.push(token_list.get(i + 1));
        the_stack_tm.push(token_list.get(i));
    }
}
