import os
import joblib

MODEL_PATH = os.path.join(os.path.dirname(__file__), "..", "models", "sentinel_pipeline_v1.pkl")
model = None

def predict_spam(text: str) -> str:
    """
    Lazy-loads the model and predicts spam/ham.
    Returns 'ham' if model is missing (for testing imports).
    """
    global model
    if model is None:
        if os.path.exists(MODEL_PATH):
            model = joblib.load(MODEL_PATH)
        else:
            return "ham"  # fallback for testing
    return model.predict([text])[0]
