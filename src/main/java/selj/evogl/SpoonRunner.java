package selj.evogl;

import selj.evogl.processors.LoggingProcessor;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonRunner {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java SpoonRunner <inputPath> <outputPath>");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        Launcher launcher = new Launcher();
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setNoClasspath(true);

        launcher.addInputResource(inputPath);
        launcher.setSourceOutputDirectory(outputPath);

        launcher.addProcessor(new LoggingProcessor());

        launcher.run();
    }
}
