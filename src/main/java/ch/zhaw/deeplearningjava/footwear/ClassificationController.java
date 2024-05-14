package ch.zhaw.deeplearningjava.footwear;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class ClassificationController {

    private Inference inference = new Inference();
    private Inference128 inference128 = new Inference128();

    @GetMapping("/ping")
    public String ping() {
        return "Classification app is up and running!";
    }

    @PostMapping(path = "/analyze32")
    public String predict(@RequestParam("image") MultipartFile image) throws Exception {
        System.out.println(image);
        return inference.predict(image.getBytes()).toJson();
    }

    @PostMapping(path = "/analyze128")
    public String predict128(@RequestParam("image") MultipartFile image) throws Exception {
        System.out.println(image);
        return inference128.predict(image.getBytes()).toJson();
    }
}