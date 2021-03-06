package lexer;

import token.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PSLexer implements Lexer {
    @Override
    public List<Token> identifyTokens(List<String> text) {
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < text.size(); i++) {
            //line to char
            List<String> currentLine = lineToChars(text.get(i));
            tokens.addAll(lineToToken(currentLine, i));
        }
        return tokens;
    }


    private List<String> lineToChars(String line) {
        List<String> chars = Arrays.asList(line.split("(?!^)"));
        return chars;
    }

    private List<Token> lineToToken(List<String> line, int lineNumber) {
        String currentWord = "";
        List<Token> list = new ArrayList<Token>();
        for (int i = 0; i < line.size(); i++) {
            Optional<Token> token;
            if (currentWord.matches("\\d+")) {
                //el regex es de  combinaciones de integer
                String intNumber = "";
                for (int j = i; j < line.size(); j++) {
                    if (line.get(j).equals(";") | line.get(j).equals(" ")) {
                        currentWord = intNumber;
                        i = j;
                        break;
                    }
                    intNumber += line.get(j);
                    i = j;
                }
                if (currentWord.matches("\\d+")) {
                    //regex de float
                    token = Optional.of(Token.integer(lineNumber, i, currentWord));
                } else  {

                    token = Optional.of(Token.floatingPoint(lineNumber, i, currentWord));
                }

            } else if (!list.isEmpty() && list.get(list.size() - 1).getValue().equals("let")) {
                String variableName = "";
                for (int j = i; j < line.size(); j++) {
                    if (line.get(j).equals(";") | line.get(j).equals(":") | line.get(j).equals(" ")) {
                        currentWord = variableName;
                        i = j;
                        break;
                    }
                    variableName += line.get(j);
                    i = j;
                }
                token = Optional.of(Token.identifier(currentWord, lineNumber, i));
            } else if (currentWord.length() > 1 && currentWord.charAt(0) == '"' && currentWord.charAt(currentWord.length() - 1) == '"') {
                token = Optional.of(Token.string(i, lineNumber, currentWord));
            } else {
                token = tokenIdentifier(currentWord, lineNumber, i);
            }
            if (token.isPresent()) {
                list.add(token.get());
                currentWord = "";
            } else currentWord += line.get(i);
        }

        return list;
    }

    private Optional<Token> tokenIdentifier(String token, int lineNumber, int columnNumber) {
        return switch (token) {
            case "let" -> Optional.of(Token.let(lineNumber, columnNumber));
            case "string" -> Optional.of(Token.stringType(lineNumber, columnNumber));
            case "number" -> Optional.of(Token.numberType(lineNumber, columnNumber));
            case "=" -> Optional.of(Token.assignation(lineNumber, columnNumber));
            case ":" -> Optional.of(Token.colon(lineNumber, columnNumber));
            case ";" -> Optional.of(Token.semicolon(lineNumber, columnNumber));
            case "-" -> Optional.of(Token.substraction(lineNumber, columnNumber));
            case "+" -> Optional.of(Token.addition(lineNumber, columnNumber));
            case "/" -> Optional.of(Token.division(lineNumber, columnNumber));
            case "*" -> Optional.of(Token.multiplication(lineNumber, columnNumber));
            default -> Optional.empty();
        };
    }
}
