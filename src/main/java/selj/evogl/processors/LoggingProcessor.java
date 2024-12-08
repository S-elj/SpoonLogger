package selj.evogl.processors;


import org.slf4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.*;



public class LoggingProcessor extends AbstractProcessor<CtClass<?>> {

    @Override
    public boolean isToBeProcessed(CtClass<?> ctClass) {
        return ctClass.getSimpleName().equals("ProductService"); //seule classe sur laquelle on souhaite ajouter des logs
    }

    @Override
    public void process(CtClass<?> ctClass) {
        addImports(ctClass);
        addLoggerField(ctClass);
        injectLogging(ctClass);
    }

    // Ajout import pour slf4j.Logger
    private void addImports(CtClass<?> ctClass) {
        CtCompilationUnit compilationUnit = ctClass.getFactory().CompilationUnit().getOrCreate(ctClass);


        CtImport loggerImport = ctClass.getFactory().Core().createImport();
        loggerImport.setReference(ctClass.getFactory().createReference("org.slf4j.Logger"));
        compilationUnit.getImports().add(loggerImport);


        CtImport loggerFactoryImport = ctClass.getFactory().Core().createImport();
        loggerFactoryImport.setReference(ctClass.getFactory().createReference("org.slf4j.LoggerFactory"));
        compilationUnit.getImports().add(loggerFactoryImport);
    }


    private void addLoggerField(CtClass<?> ctClass) {
        // attribut privé statique et final pour le logger
        CtField<?> userLoggerField = getFactory().createField();
        userLoggerField.setType(getFactory().createCtTypeReference(Logger.class));
        userLoggerField.setSimpleName("userLogger");
        userLoggerField.addModifier(ModifierKind.PRIVATE);
        userLoggerField.addModifier(ModifierKind.STATIC);
        userLoggerField.addModifier(ModifierKind.FINAL);
        userLoggerField.setDefaultExpression(getFactory().createCodeSnippetExpression(
                "org.slf4j.LoggerFactory.getLogger(\"com.example.user\")"
        ));


        ctClass.addFieldAtTop(userLoggerField);
    }

    private void injectLogging(CtClass<?> ctClass) {
        for (CtMethod<?> method : ctClass.getMethods()) {
            if (method.getBody() != null) {
                // Déterminer le type d'opération en fonction du nom de la méthode
                String operationType = getOperationType(method.getSimpleName());

                // Log de base avec les informations utilisateur
                String logMessage = String.format(
                        "userLogger.info(\"{ \\\"user\\\": \"+ userService.getLoggedInUserJson() + \", " + //getLoggedInUserJson()  est definie dans le projet spring
                                "\\\"operation\\\": \\\"%s\\\", " +
                                "\\\"method\\\": \\\"%s\\\", " +
                                "\\\"timestamp\\\": \\\"\" + java.time.Instant.now() + \"\\\" ",
                        operationType, method.getSimpleName()
                );

                // Si l'opération est de type "READ", on ajoute les informations du produit
                if ("READ".equals(operationType)) {
                    boolean hasNameParam = false;

                    // Vérifier les paramètres de la méthode (infos produit accecible)
                    for (CtParameter<?> param : method.getParameters()) {
                        if (param.getType().getSimpleName().equals("String") && param.getSimpleName().equals("name")) {
                            hasNameParam = true;
                            break;
                        }
                    }

                    // Ajouter les informations du produit dans le log
                    if (hasNameParam) {
                        String productLogMessage =
                                ", \\\"product_name\\\": \\\"\" + name + \"\\\", " +
                                        "\\\"product_price\\\": \\\"\" + getProductPrice(name) + \"\\\" "; // getProductPrice est definie dans le projet spring

                        logMessage += productLogMessage; // Fusionner les deux logs
                    }
                }

                logMessage += " } \");"; // Fermer le log correctement

                CtStatement logStatement = getFactory().createCodeSnippetStatement(logMessage);

                // Injecter le log au début de la méthode
                method.getBody().insertBegin(logStatement);
            }
        }


    }

    //Puisqu'on traite d'une classe Service on peut se servir des conventions de nommage
    private String getOperationType(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("fetch") || methodName.startsWith("find")) {
            return "READ";
        } else if (methodName.startsWith("create") || methodName.startsWith("add")) {
            return "WRITE";
        } else if (methodName.startsWith("delete")) {
            return "DELETE";
        } else if (methodName.startsWith("update")) {
            return "UPDATE";
        }
        return "UNKNOWN";
    }




}
