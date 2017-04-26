package mcjty.enigma.parser;

import mcjty.enigma.Enigma;
import mcjty.enigma.code.Scope;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RuleParser {

    public static List<TokenizedLine> parse(File file, @Nonnull ExpressionContext expressionContext) throws ParserException {
        if (!file.exists()) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return parse(reader, expressionContext);
        } catch (FileNotFoundException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        } catch (IOException e) {
            Enigma.logger.log(Level.ERROR, "Error reading file " + file.getName());
            throw new RuntimeException("Error reading file: " + file.getName());
        }
    }

    public static List<TokenizedLine> parse(BufferedReader reader, ExpressionContext expressionContext) throws IOException, ParserException {
        List<TokenizedLine> lines = new ArrayList<>();
        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            TokenizedLine tokenizedLine = getTokenizedLine(line, i, expressionContext);
            if (tokenizedLine != null) {
                tokenizedLine.dump();
                lines.add(tokenizedLine);
            }
            line = reader.readLine();
            i++;
        }
        return lines;
    }

    private static TokenizedLine getTokenizedLine(String line, int linenumber, ExpressionContext expressionContext) throws ParserException {
        int indentation = 0;
        int i = 0;
        while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
            if (line.charAt(i) == '\t') {
                indentation = (indentation + 8) % 8;
            } else {
                indentation++;
            }
            i++;
        }
        line = line.trim();
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }

        StringPointer str = new StringPointer(line);
        String token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).eval(null));

        int parameters = 0;

        MainToken mainToken = MainToken.getTokenByName(token);
        if (mainToken == null) {
            throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
        }
        parameters = mainToken.getParameters();

        Token secondaryToken = null;
        if (mainToken.isHasSecondaryToken()) {
            token = ObjectTools.asStringSafe(ExpressionParser.eval(str, new EmptyExpressionContext()).eval(null));
            secondaryToken = Token.getTokenByName(token);
            if (secondaryToken == null) {
                throw new ParserException("ERROR: Unknown token '" + token + "'!", linenumber);
            }
            parameters = secondaryToken.getParameters();
        }

        List<Expression> params = new ArrayList<>(parameters);
        for (int t = 0 ; t < parameters ; t++) {
            Expression expression = ExpressionParser.eval(str, expressionContext);
            params.add(expression);
        }
        boolean endsWithColon = str.hasMore() && str.current() == ':';

        return new TokenizedLine(indentation, linenumber, mainToken, secondaryToken, params, endsWithColon);
    }

    public static void main(String[] args) {
        String dir = System.getProperty("user.dir");
        System.out.println("dir = " + dir);
        File f = new File("out/production/enigma/assets/enigma/rules/ruleexample");

        try {
            List<TokenizedLine> lines = parse(f, new EmptyExpressionContext());
            Scope root = ProgramParser.parse(lines);
            root.dump(0);
        } catch (ParserException e) {
            System.out.println("e.getMessage() = " + e.getMessage() + " at line " + e.getLinenumber());
        }

        StringPointer str = new StringPointer("double(1)*var   8/2!=2+2 sqrt 16 \"Dit is \\\"een\\\" test\"+' (echt)' 'nog eentje' blub");
        ExpressionContext context = new ExpressionContext() {
            @Nullable
            @Override
            public Expression getVariable(String var) {
                return "var".equals(var) ? w -> 666 : null;
            }

            @Override
            public boolean isVariable(String var) {
                return "var".equals(var);
            }

            @Nullable
            @Override
            public ExpressionFunction getFunction(String name) {
                return "double".equals(name) ? (w,o) -> ObjectTools.asIntSafe(o) * 2 : null;
            }

            @Override
            public boolean isFunction(String name) {
                return "double".equals(name);
            }
        };
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
        System.out.println("result = " + ExpressionParser.eval(str, context).eval(null));
    }
}
