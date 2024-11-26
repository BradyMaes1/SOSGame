import torch
from flask import Flask, request, jsonify
from transformers import pipeline

app = Flask(__name__)

# Load the Gemma model pipeline
pipe = pipeline(
    "text-generation",
    model="google/gemma-2-9b",
    model_kwargs={"torch_dtype": torch.bfloat16},
    device="cpu",  # Adjust if running on a Mac device or CPU
)

@app.route("/generate", methods=["POST"])
def generate_text():
    try:
        data = request.json
        prompt = data.get("prompt", "")
        max_length = data.get("max_length", 256)

        # Pass the prompt to the model pipeline
        messages = [{"role": "user", "content": prompt}]
        outputs = pipe(messages, return_full_text=False, max_new_tokens=max_length)

        # Extract the generated text
        assistant_response = outputs[0]["generated_text"].strip()
        return jsonify({"generated_text": assistant_response})
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == "__main__":
    app.run(host="127.0.0.1", port=5000)
