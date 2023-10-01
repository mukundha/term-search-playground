package com.example.demo.controller;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.analysis.util.TokenFilterFactory;

@Controller
public class AnalyzerController {

    Set<String> availableTokenizers;
    Set<String> availableFilters;
    @GetMapping("/")
    public String index(Model model) {
        // Initialize and populate tokenizers and filters, and pass them to the view
        availableTokenizers = TokenizerFactory.availableTokenizers(); // Populate with Lucene tokenizer classes
        availableFilters = TokenFilterFactory.availableTokenFilters(); // Populate with Lucene filter classes

        model.addAttribute("availableTokenizers", availableTokenizers);
        model.addAttribute("availableFilters", availableFilters);

        return "index";
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam String text,
            @RequestParam String tokenizerClass,
            @RequestParam List<String> filterClasses,
            Model model) throws IOException {
        // Create a custom Lucene analyzer based on user selections
        CustomAnalyzer.Builder builder = CustomAnalyzer.builder()
                                            .withTokenizer(tokenizerClass);
        for (String filter: filterClasses){
            builder.addTokenFilter(filter);
        }
        Analyzer customAnalyzer = builder.build();

        // Analyze the input text
        TokenStream ts = customAnalyzer.tokenStream("myfield", new StringReader(text));
        CharTermAttribute offsetAtt = ts.addAttribute(CharTermAttribute.class);
        ArrayList<String> tokens = new ArrayList<>();
        try {
            ts.reset(); // Resets this stream to the beginning. (Required)
            while (ts.incrementToken()) {
              // Use AttributeSource.reflectAsString(boolean)
              // for token stream debugging.
              String t = offsetAtt.toString();
              System.out.println("token: " + t );         
              tokens.add(t);
            }
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.
            ts.close(); // Release resources associated with this stream.
          } catch(Exception e){
             System.out.println(e);
          }finally {
            
          }
        model.addAttribute("availableTokenizers", availableTokenizers);
        model.addAttribute("availableFilters", availableFilters);
        model.addAttribute("analyzedText", Arrays.toString(tokens.toArray()));
        return "index";
    }

    // Implement createCustomAnalyzer and analyzeText methods as discussed in previous responses
}
