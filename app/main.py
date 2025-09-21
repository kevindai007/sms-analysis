from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api import sms_router
from app.database import engine, Base

# Create tables
Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="SMS Analysis Platform",
    description="短信分析平台后台 - 分析用户上传的短信信息",
    version="1.0.0"
)

# Configure CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(sms_router.router, prefix="/api/v1", tags=["sms"])

@app.get("/")
async def root():
    return {"message": "SMS Analysis Platform API", "version": "1.0.0"}

@app.get("/health")
async def health():
    return {"status": "healthy"}