from fastapi import FastAPI
from app.routers import spam

app = FastAPI(title="SentinelIQ Spam Classifier")

# Include spam router
app.include_router(spam.router, prefix="/spam")
