from fastapi import APIRouter, Depends, HTTPException, UploadFile, File
from sqlalchemy.orm import Session
from typing import List
import json
import csv
import io

from app.database import get_db, SMSRecord
from app.schemas import SMSCreate, SMSResponse, SMSBatchUpload, AnalysisStats
from app.analysis import SMSAnalyzer

router = APIRouter()
analyzer = SMSAnalyzer()

@router.post("/sms", response_model=SMSResponse)
async def create_sms(sms: SMSCreate, db: Session = Depends(get_db)):
    """上传单条短信并进行分析"""
    
    # Analyze the SMS
    analysis_result = analyzer.analyze_sms(sms.message_content)
    
    # Create database record
    db_sms = SMSRecord(
        phone_number=sms.phone_number,
        message_content=sms.message_content,
        message_type=sms.message_type,
        timestamp=sms.timestamp,
        sentiment_score=analysis_result['sentiment_score'],
        sentiment_label=analysis_result['sentiment_label'],
        keywords=json.dumps(analysis_result['keywords'], ensure_ascii=False),
        category=analysis_result['category'],
        length=analysis_result['word_count']
    )
    
    db.add(db_sms)
    db.commit()
    db.refresh(db_sms)
    
    return db_sms

@router.post("/sms/batch", response_model=List[SMSResponse])
async def create_sms_batch(batch: SMSBatchUpload, db: Session = Depends(get_db)):
    """批量上传短信并进行分析"""
    
    results = []
    for sms in batch.sms_list:
        analysis_result = analyzer.analyze_sms(sms.message_content)
        
        db_sms = SMSRecord(
            phone_number=sms.phone_number,
            message_content=sms.message_content,
            message_type=sms.message_type,
            timestamp=sms.timestamp,
            sentiment_score=analysis_result['sentiment_score'],
            sentiment_label=analysis_result['sentiment_label'],
            keywords=json.dumps(analysis_result['keywords'], ensure_ascii=False),
            category=analysis_result['category'],
            length=analysis_result['word_count']
        )
        
        db.add(db_sms)
        results.append(db_sms)
    
    db.commit()
    
    for sms in results:
        db.refresh(sms)
    
    return results

@router.post("/sms/upload-csv")
async def upload_csv(file: UploadFile = File(...), db: Session = Depends(get_db)):
    """通过CSV文件批量上传短信"""
    
    if not file.filename.endswith('.csv'):
        raise HTTPException(status_code=400, detail="文件必须是CSV格式")
    
    content = await file.read()
    csv_data = content.decode('utf-8')
    
    # Parse CSV
    csv_reader = csv.DictReader(io.StringIO(csv_data))
    
    results = []
    for row in csv_reader:
        if 'phone_number' not in row or 'message_content' not in row:
            continue
            
        analysis_result = analyzer.analyze_sms(row['message_content'])
        
        db_sms = SMSRecord(
            phone_number=row['phone_number'],
            message_content=row['message_content'],
            message_type=row.get('message_type', 'received'),
            sentiment_score=analysis_result['sentiment_score'],
            sentiment_label=analysis_result['sentiment_label'],
            keywords=json.dumps(analysis_result['keywords'], ensure_ascii=False),
            category=analysis_result['category'],
            length=analysis_result['word_count']
        )
        
        db.add(db_sms)
        results.append(db_sms)
    
    db.commit()
    
    return {"message": f"成功上传 {len(results)} 条短信记录"}

@router.get("/sms", response_model=List[SMSResponse])
async def get_sms_list(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    """获取短信列表"""
    sms_records = db.query(SMSRecord).offset(skip).limit(limit).all()
    return sms_records

@router.get("/sms/{sms_id}", response_model=SMSResponse)
async def get_sms(sms_id: int, db: Session = Depends(get_db)):
    """获取单条短信详情"""
    sms = db.query(SMSRecord).filter(SMSRecord.id == sms_id).first()
    if sms is None:
        raise HTTPException(status_code=404, detail="短信记录未找到")
    return sms

@router.get("/sms/phone/{phone_number}", response_model=List[SMSResponse])
async def get_sms_by_phone(phone_number: str, db: Session = Depends(get_db)):
    """根据手机号获取短信列表"""
    sms_records = db.query(SMSRecord).filter(SMSRecord.phone_number == phone_number).all()
    return sms_records

@router.get("/analysis/stats", response_model=AnalysisStats)
async def get_analysis_stats(db: Session = Depends(get_db)):
    """获取分析统计数据"""
    
    # Total messages
    total_messages = db.query(SMSRecord).count()
    
    # Sentiment distribution
    sentiment_counts = db.query(SMSRecord.sentiment_label).all()
    sentiment_distribution = {}
    for sentiment, in sentiment_counts:
        sentiment_distribution[sentiment] = sentiment_distribution.get(sentiment, 0) + 1
    
    # Category distribution
    category_counts = db.query(SMSRecord.category).all()
    category_distribution = {}
    for category, in category_counts:
        category_distribution[category] = category_distribution.get(category, 0) + 1
    
    # Top keywords
    all_keywords = []
    keyword_records = db.query(SMSRecord.keywords).filter(SMSRecord.keywords.isnot(None)).all()
    for keywords_json, in keyword_records:
        try:
            keywords = json.loads(keywords_json)
            all_keywords.extend(keywords)
        except:
            continue
    
    from collections import Counter
    keyword_counter = Counter(all_keywords)
    top_keywords = [{"keyword": k, "count": v} for k, v in keyword_counter.most_common(10)]
    
    # Average length
    lengths = db.query(SMSRecord.length).filter(SMSRecord.length.isnot(None)).all()
    average_length = sum(length for length, in lengths) / len(lengths) if lengths else 0
    
    return AnalysisStats(
        total_messages=total_messages,
        sentiment_distribution=sentiment_distribution,
        top_keywords=top_keywords,
        category_distribution=category_distribution,
        average_length=average_length
    )

@router.delete("/sms/{sms_id}")
async def delete_sms(sms_id: int, db: Session = Depends(get_db)):
    """删除短信记录"""
    sms = db.query(SMSRecord).filter(SMSRecord.id == sms_id).first()
    if sms is None:
        raise HTTPException(status_code=404, detail="短信记录未找到")
    
    db.delete(sms)
    db.commit()
    
    return {"message": "短信记录已删除"}