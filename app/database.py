from sqlalchemy import create_engine, Column, Integer, String, DateTime, Text, Float
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from datetime import datetime

# SQLite database for simplicity
SQLALCHEMY_DATABASE_URL = "sqlite:///./sms_analysis.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class SMSRecord(Base):
    __tablename__ = "sms_records"

    id = Column(Integer, primary_key=True, index=True)
    phone_number = Column(String, index=True)
    message_content = Column(Text, nullable=False)
    timestamp = Column(DateTime, default=datetime.utcnow)
    message_type = Column(String, default="received")  # received/sent
    
    # Analysis results
    sentiment_score = Column(Float, nullable=True)
    sentiment_label = Column(String, nullable=True)
    keywords = Column(Text, nullable=True)  # JSON string
    category = Column(String, nullable=True)
    length = Column(Integer, nullable=True)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()