package com.parsingVAST.Controller;


import com.parsingVAST.Model.VastData;
import com.parsingVAST.Service.VastParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vast")
public class VastController {

    @Autowired
    private VastParsingService vastParsingService;

    // Endpoint to read from a URL and save the parsed data
    @PostMapping("/parse-url")
    public ResponseEntity<String> parseFromUrl(@RequestParam String url) {
        try {
            vastParsingService.parseAndSaveFromUrl(url);
            return ResponseEntity.ok("VAST data parsed and saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing VAST data from URL: " + e.getMessage());
        }
    }

    // Endpoint to read from a file and save the parsed data
    @PostMapping("/parse-file")
    public ResponseEntity<String> parseFromFile(@RequestParam String filePath) {
        try {
            vastParsingService.parseAndSaveFromFile(filePath);
            return ResponseEntity.ok("VAST data parsed and saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error parsing VAST data from file: " + e.getMessage());
        }
    }

    // Get all parsed VAST data
    @GetMapping("/all")
    public ResponseEntity<List<VastData>> getAllVastData() {
        return ResponseEntity.ok(vastParsingService.getAllVastData());
    }

    // Get a single VAST data by ID
    @GetMapping("/{id}")
    public ResponseEntity<VastData> getVastDataById(@PathVariable String id) {
        VastData vastData = vastParsingService.getVastDataById(id);
        if (vastData != null) {
            return ResponseEntity.ok(vastData);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}

