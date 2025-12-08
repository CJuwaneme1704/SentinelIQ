from fastapi import APIRouter
from pydantic import BaseModel
from app.services.spam_service import predict_spam

router = APIRouter()

class SpamRequest(BaseModel):
    text: str

@router.post("/predict")
def predict_spam_route(request: SpamRequest):
    return {"prediction": predict_spam(request.text)}
