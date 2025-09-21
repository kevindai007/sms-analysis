from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime

class SMSBase(BaseModel):
    phone_number: str = Field(..., description="手机号码")
    message_content: str = Field(..., description="短信内容")
    message_type: Optional[str] = Field("received", description="短信类型: received/sent")

class SMSCreate(SMSBase):
    timestamp: Optional[datetime] = None

class SMSResponse(SMSBase):
    id: int
    timestamp: datetime
    sentiment_score: Optional[float] = None
    sentiment_label: Optional[str] = None
    keywords: Optional[str] = None
    category: Optional[str] = None
    length: Optional[int] = None
    
    class Config:
        from_attributes = True

class SMSAnalysisResult(BaseModel):
    sentiment_score: float = Field(..., description="情感分数 (-1到1)")
    sentiment_label: str = Field(..., description="情感标签: positive/negative/neutral")
    keywords: List[str] = Field(..., description="关键词列表")
    category: str = Field(..., description="短信分类")
    word_count: int = Field(..., description="字数统计")

class SMSBatchUpload(BaseModel):
    sms_list: List[SMSCreate] = Field(..., description="批量短信数据")

class AnalysisStats(BaseModel):
    total_messages: int
    sentiment_distribution: dict
    top_keywords: List[dict]
    category_distribution: dict
    average_length: float