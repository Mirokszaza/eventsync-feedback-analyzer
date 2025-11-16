# EventSync — Event Feedback Analyzer (IBM Internship Exercise)

## What this project does
- Create and list events (title + description)
- Submit feedback for a specific event
- Send feedback text to a Hugging Face sentiment model
- Store feedback and sentiment in an H2 in-memory database
- Return a simple summary (positive/neutral/negative counts and percentages) per event
- Provides Swagger UI to test the API

## How to run

### 1. (Optional) Set Hugging Face token
```bash
# Windows PowerShell
$env:HUGGING_FACE_TOKEN="hf_your_token_here"

# Linux/macOS
export HUGGING_FACE_TOKEN=hf_your_token_here
