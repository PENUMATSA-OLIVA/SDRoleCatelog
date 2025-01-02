import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    public static BigInteger calculateLagrange(List<Integer> indices, List<BigInteger> values, int numPoints) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < numPoints; i++) {
            BigInteger term = values.get(i);

            for (int j = 0; j < numPoints; j++) {
                if (i != j) {
                    BigInteger numerator = BigInteger.valueOf(-indices.get(j));
                    BigInteger denominator = BigInteger.valueOf(indices.get(i) - indices.get(j));
                    term = term.multiply(numerator).divide(denominator);
                }
            }

            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("input1.json"));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line.trim());
            }
            reader.close();

            String json = jsonBuilder.toString();

            int totalPoints = Integer.parseInt(json.split("\"n\":")[1].split(",")[0].trim());
            int requiredPoints = Integer.parseInt(json.split("\"k\":")[1].split("}")[0].trim());

            if (requiredPoints > totalPoints) {
                System.out.println("Error: k cannot be greater than n.");
                return;
            }

            List<Integer> indices = new ArrayList<>();
            List<BigInteger> values = new ArrayList<>();

            for (int i = 1; i < totalPoints; i++) {
                String index = "\"" + i + "\":";
                int startIndex = json.indexOf(index) + index.length();
                int endIndex = json.indexOf("}", startIndex);

                String point = json.substring(startIndex, endIndex + 1);

                String baseStr = point.split("\"base\":")[1].split(",")[0].trim();
                if (baseStr.isEmpty()) {
                    System.out.println("Failed to extract base for index " + i);
                    continue;
                }

                baseStr = baseStr.replaceAll("\"", "").trim();
                int base = Integer.parseInt(baseStr);

                String valueStr = point.split("\"value\":")[1].split("\"")[1].trim();
                if (valueStr.isEmpty()) {
                    System.out.println("Failed to extract value for index " + i);
                    continue;
                }

                valueStr = valueStr.replaceAll("\"", "").trim();

                indices.add(i);
                values.add(decodeValue(valueStr, base));
            }

            if (indices.size() < requiredPoints || values.size() < requiredPoints) {
                System.out.println("Error: Insufficient data for interpolation. indices or values size is less than k.");
                return;
            }

            BigInteger constantTerm = calculateLagrange(indices.subList(0, requiredPoints), values.subList(0, requiredPoints), requiredPoints);

            System.out.println("The constant term (f(0)) is: " + constantTerm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}