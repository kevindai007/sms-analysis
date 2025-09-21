from app.analysis import SMSAnalyzer

def test_sentiment_analysis():
    analyzer = SMSAnalyzer()
    
    # Test positive sentiment
    score, label = analyzer.analyze_sentiment("感谢您的购买，服务很棒，非常满意！")
    assert score > 0
    assert label == "positive"
    
    # Test negative sentiment 
    score, label = analyzer.analyze_sentiment("服务很差，很失望，真的很糟糕")
    assert score < 0
    assert label == "negative"
    
    # Test neutral sentiment
    score, label = analyzer.analyze_sentiment("今天是星期一")
    assert label == "neutral"

def test_keyword_extraction():
    analyzer = SMSAnalyzer()
    
    text = "您的验证码是123456，请在5分钟内使用"
    keywords = analyzer.extract_keywords(text)
    
    assert "验证码" in keywords
    assert "123456" in keywords
    assert len(keywords) > 0

def test_message_categorization():
    analyzer = SMSAnalyzer()
    
    # Test verification code
    assert analyzer.categorize_message("您的验证码是123456") == "验证码"
    
    # Test marketing
    assert analyzer.categorize_message("限时优惠，全场8折") == "营销推广"
    
    # Test notification
    assert analyzer.categorize_message("提醒您明天有会议") == "通知提醒"
    
    # Test customer service
    assert analyzer.categorize_message("客服为您服务") == "客服服务"
    
    # Test other
    assert analyzer.categorize_message("今天天气不错") == "其他"

def test_complete_analysis():
    analyzer = SMSAnalyzer()
    
    text = "感谢您的购买！我们的服务非常好"
    result = analyzer.analyze_sms(text)
    
    assert "sentiment_score" in result
    assert "sentiment_label" in result
    assert "keywords" in result
    assert "category" in result
    assert "word_count" in result
    
    assert result["sentiment_label"] == "positive"
    assert result["word_count"] == len(text)