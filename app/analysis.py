import jieba
import json
import re
from typing import List
from collections import Counter

class SMSAnalyzer:
    def __init__(self):
        # Initialize jieba for Chinese text segmentation
        jieba.initialize()
        
        # Sentiment keywords (simplified approach)
        self.positive_words = {
            '好', '棒', '赞', '爱', '喜欢', '开心', '快乐', '高兴', '满意', '优秀', 
            '成功', '感谢', '谢谢', '完美', '太好了', '不错', '牛', '厉害'
        }
        
        self.negative_words = {
            '坏', '差', '烂', '恨', '讨厌', '生气', '愤怒', '失望', '糟糕', '垃圾',
            '问题', '故障', '错误', '失败', '抱歉', '对不起', '麻烦', '困难'
        }
        
        # SMS categories based on common patterns
        self.category_patterns = {
            '验证码': [r'\d{4,6}', '验证码', '验证', 'code', '登录'],
            '营销推广': ['促销', '优惠', '折扣', '活动', '购买', '商品', '店铺'],
            '通知提醒': ['提醒', '通知', '账单', '还款', '到期', '会议', '预约'],
            '客服服务': ['客服', '服务', '咨询', '帮助', '支持', '回复'],
            '其他': []
        }
    
    def analyze_sentiment(self, text: str) -> tuple:
        """分析文本情感"""
        words = list(jieba.cut(text))
        
        positive_count = sum(1 for word in words if word in self.positive_words)
        negative_count = sum(1 for word in words if word in self.negative_words)
        
        # Calculate sentiment score (-1 to 1)
        total_sentiment_words = positive_count + negative_count
        if total_sentiment_words == 0:
            score = 0.0
            label = "neutral"
        else:
            score = (positive_count - negative_count) / max(total_sentiment_words, 1)
            if score > 0.1:
                label = "positive"
            elif score < -0.1:
                label = "negative"
            else:
                label = "neutral"
        
        return score, label
    
    def extract_keywords(self, text: str, top_k: int = 10) -> List[str]:
        """提取关键词"""
        words = list(jieba.cut(text))
        # Filter out single characters and common stop words
        stop_words = {'的', '了', '在', '是', '我', '你', '他', '她', '它', '们', '这', '那'}
        filtered_words = [word for word in words if len(word) > 1 and word not in stop_words]
        
        # Count word frequency
        word_counts = Counter(filtered_words)
        return [word for word, count in word_counts.most_common(top_k)]
    
    def categorize_message(self, text: str) -> str:
        """分类短信"""
        text_lower = text.lower()
        
        for category, keywords in self.category_patterns.items():
            if category == '其他':
                continue
            
            for keyword in keywords:
                if isinstance(keyword, str):
                    if keyword in text_lower:
                        return category
                else:  # regex pattern
                    if re.search(keyword, text):
                        return category
        
        return '其他'
    
    def analyze_sms(self, text: str) -> dict:
        """完整的短信分析"""
        sentiment_score, sentiment_label = self.analyze_sentiment(text)
        keywords = self.extract_keywords(text)
        category = self.categorize_message(text)
        word_count = len(text)
        
        return {
            'sentiment_score': sentiment_score,
            'sentiment_label': sentiment_label,
            'keywords': keywords,
            'category': category,
            'word_count': word_count
        }