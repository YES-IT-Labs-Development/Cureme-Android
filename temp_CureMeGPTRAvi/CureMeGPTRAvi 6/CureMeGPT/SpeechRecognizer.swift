//
//  SpeechRecognizer.swift
//  CureMeGPT
//
//  Created by YATIN  KALRA on 16/04/26.
//


import Foundation
import Speech
import AVFoundation

class SpeechRecognizer: NSObject, ObservableObject {
    
    @Published var Voicetext: String = ""
    @Published var isRecording = false
    
    private let recognizer = SFSpeechRecognizer()
    private var request: SFSpeechAudioBufferRecognitionRequest?
    private var task: SFSpeechRecognitionTask?
    private let audioEngine = AVAudioEngine()
    
    private var isManuallyStopped = false
    
    override init() {
        super.init()
        requestPermission()
    }
    
    // MARK: - Permission
    func requestPermission() {
        SFSpeechRecognizer.requestAuthorization { status in
            DispatchQueue.main.async {
                if status != .authorized {
                    print("Speech permission denied")
                }
            }
        }
    }
    
    // MARK: - START
    func start() {
        guard !isRecording else { return }
        
        isRecording = true
        isManuallyStopped = false
        Voicetext = ""
        
        // Cancel previous task
        task?.cancel()
        task = nil
        
        request = SFSpeechAudioBufferRecognitionRequest()
        guard let request = request else { return }
        
        request.shouldReportPartialResults = true
        
        // ✅ BEST AUDIO CONFIG (NO ECHO)
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(.playAndRecord,
                                         mode: .voiceChat,   // 🔥 echo cancel
                                         options: [.duckOthers, .defaultToSpeaker])
            
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        } catch {
            print("AudioSession error:", error)
        }
        
        let inputNode = audioEngine.inputNode
        let format = inputNode.outputFormat(forBus: 0)
        
        inputNode.removeTap(onBus: 0)
        
        inputNode.installTap(onBus: 0,
                             bufferSize: 1024,
                             format: format) { buffer, _ in
            request.append(buffer)
        }
        
        audioEngine.prepare()
        
        do {
            try audioEngine.start()
        } catch {
            print("Audio engine start error:", error)
            return
        }
        
        task = recognizer?.recognitionTask(with: request) { [weak self] result, error in
            guard let self = self else { return }
            
            if self.isManuallyStopped || !self.isRecording { return }
            
            if let result = result {
                DispatchQueue.main.async {
                    self.Voicetext = result.bestTranscription.formattedString
                }
            }
            
            if error != nil {
                self.stop()
            }
        }
    }
    
    // MARK: - STOP
    func stop() {
        guard isRecording else { return }
        
        isRecording = false
        isManuallyStopped = true
        
        audioEngine.stop()
        audioEngine.inputNode.removeTap(onBus: 0)
        
        request?.endAudio()
        
        task?.finish()
        task = nil
        request = nil
        
        try? AVAudioSession.sharedInstance().setActive(false)
    }
    
 
    
    func reset() {
        Voicetext = ""
        isRecording = false
    }
}
