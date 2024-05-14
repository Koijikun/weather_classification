
package ch.zhaw.deeplearningjava.footwear;

import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.metric.Metrics;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.types.Shape;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingConfig;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.RandomAccessDataset;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Training {

    // represents number of training samples processed before the model is updated
    private static final int BATCH_SIZE = 32;

    // the number of passes over the complete dataset
    private static final int EPOCHS = 3;

    public static void main(String[] args) throws IOException, TranslateException {
        // the location to save the model
        Path modelDir = Paths.get("models");

        // weather_data128 trains with 128x128, weather_data trains with 32x32
        String datafolder = "weather_data128";
        ImageFolder dataset = initDataset(datafolder);
        // Split the dataset set into training dataset and validate dataset
        RandomAccessDataset[] datasets = dataset.randomSplit(9, 1);

        // set loss function, which seeks to minimize errors
        // loss function evaluates model's predictions against the correct answer
        // (during training)
        // higher numbers are bad - means model performed poorly; indicates more errors;
        // want to
        // minimize errors (loss)
        Loss loss = Loss.softmaxCrossEntropyLoss();

        // setting training parameters (ie hyperparameters)
        TrainingConfig config = setupTrainingConfig(loss);

        Model model;
        Shape inputShape;
        if (datafolder.equals("weather_data128")) {
            model = Models128.getModel();
            inputShape = new Shape(1, 3, Models128.IMAGE_HEIGHT, Models128.IMAGE_HEIGHT);

        } else {
            model = Models.getModel();
            inputShape = new Shape(1, 3, Models.IMAGE_HEIGHT, Models.IMAGE_HEIGHT);
        }
        
        Trainer trainer = model.newTrainer(config);
        // metrics collect and report key performance indicators, like accuracy
        trainer.setMetrics(new Metrics());

        // initialize trainer with proper input shape
        trainer.initialize(inputShape);

        // find the patterns in data
        EasyTrain.fit(trainer, EPOCHS, datasets[0], datasets[1]);

        // set model properties
        TrainingResult result = trainer.getTrainingResult();
        model.setProperty("Epoch", String.valueOf(EPOCHS));
        model.setProperty(
                "Accuracy", String.format("%.5f", result.getValidateEvaluation("Accuracy")));
        model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));

        // save the model after done training for inference later

        
        Path modelSubDir;
        if (datafolder.equals("weather_data128")) {
            modelSubDir = modelDir.resolve("weatherclassifier128");
        } else {
            modelSubDir = modelDir.resolve("weatherclassifier32");
        }

        if (Files.notExists(modelSubDir)) {
            Files.createDirectories(modelSubDir);
        }

        model.save(modelSubDir, Models.MODEL_NAME);

        // save labels into model directory
        Models.saveSynset(modelSubDir, dataset.getSynset());

    }

    private static ImageFolder initDataset(String datasetRoot)
            throws IOException, TranslateException {
        ImageFolder dataset = ImageFolder.builder()
                // retrieve the data
                .setRepositoryPath(Paths.get(datasetRoot))
                .optMaxDepth(10)
                .addTransform(new ToTensor())
                // random sampling; don't process the data in order, preformatted imgs so no need to resize
                .setSampling(BATCH_SIZE, true)
                .build();

        dataset.prepare();
        return dataset;
    }

    private static TrainingConfig setupTrainingConfig(Loss loss) {
        return new DefaultTrainingConfig(loss)
                .addEvaluator(new Accuracy())
                .addTrainingListeners(TrainingListener.Defaults.logging());
    }
}
