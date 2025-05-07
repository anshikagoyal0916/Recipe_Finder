import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DecisionTreeRecipeFinderUI extends JFrame {
    private DefaultListModel<String> ingredientListModel;
    private JList<String> ingredientList;
    private JTextField ingredientInput;
    private JTextArea resultArea;
    private java.util.List<Recipe> allRecipes;

    static class Recipe {
        String name;
        Set<String> ingredients;

        Recipe(String name, Set<String> ingredients) {
            this.name = name;
            this.ingredients = ingredients;
        }
    }

    public DecisionTreeRecipeFinderUI() {
        setTitle("Recipe Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);

        // Create main panels
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create components
        ingredientListModel = new DefaultListModel<>();
        ingredientList = new JList<>(ingredientListModel);
        ingredientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        ingredientInput = new JTextField(20);
        JButton addButton = new JButton("Add Ingredient");
        JButton removeButton = new JButton("Remove Selected");
        JButton findRecipesButton = new JButton("Find Recipes");
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);

        // Set up left panel (ingredients)
        leftPanel.add(new JLabel("Ingredients List:"), BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(ingredientList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 400));

        // Set up right panel (results)
        rightPanel.add(new JLabel("Matching Recipes:"), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        
        // Set up input panel
        inputPanel.add(new JLabel("Enter Ingredient:"), BorderLayout.WEST);
        inputPanel.add(ingredientInput, BorderLayout.CENTER);
        
        // Set up button panel
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(findRecipesButton);

        // Add panels to frame
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Load recipes
        allRecipes = loadRecipes("recipes_250.csv");

        // Add button listeners
        addButton.addActionListener(e -> addIngredient());
        removeButton.addActionListener(e -> removeIngredient());
        findRecipesButton.addActionListener(e -> findRecipes());

        // Add enter key listener to input field
        ingredientInput.addActionListener(e -> addIngredient());

        // Set margins and padding
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void addIngredient() {
        String ingredient = ingredientInput.getText().trim().toLowerCase();
        if (!ingredient.isEmpty() && !ingredientListModel.contains(ingredient)) {
            ingredientListModel.addElement(ingredient);
            ingredientInput.setText("");
        }
        ingredientInput.requestFocus();
    }

    private void removeIngredient() {
        int selectedIndex = ingredientList.getSelectedIndex();
        if (selectedIndex != -1) {
            ingredientListModel.remove(selectedIndex);
        }
    }

    private void findRecipes() {
        Set<String> givenIngredients = new HashSet<>();
        for (int i = 0; i < ingredientListModel.size(); i++) {
            givenIngredients.add(ingredientListModel.getElementAt(i));
        }

        java.util.List<Recipe> possibleRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            int matchCount = 0;
            for (String ing : givenIngredients) {
                if (recipe.ingredients.contains(ing)) {
                    matchCount++;
                }
            }
            if (matchCount >= 2) {
                possibleRecipes.add(recipe);
            }
        }

        // Display results
        StringBuilder result = new StringBuilder();
        if (possibleRecipes.isEmpty()) {
            result.append("No recipes found with at least 2 matching ingredients.");
        } else {
            result.append("Based on your ingredients, you can make:\n\n");
            for (Recipe recipe : possibleRecipes) {
                result.append("- ").append(recipe.name).append("\n");
                result.append("  Ingredients: ").append(String.join(", ", recipe.ingredients)).append("\n\n");
            }
        }
        resultArea.setText(result.toString());
    }

    private static java.util.List<Recipe> loadRecipes(String fileName) {
        java.util.List<Recipe> recipes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String[] ingArray = parts[1].split(",");
                Set<String> ingredients = new HashSet<>();

                for (String ing : ingArray) {
                    ingredients.add(ing.trim().toLowerCase());
                }

                recipes.add(new Recipe(name, ingredients));
            }
        } catch (IOException e) {
            System.out.println("Error loading recipes: " + e.getMessage());
        }
        return recipes;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DecisionTreeRecipeFinderUI frame = new DecisionTreeRecipeFinderUI();
            frame.setVisible(true);
        });
    }
}