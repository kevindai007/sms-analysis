import pytest
from fastapi.testclient import TestClient

def test_root_endpoint(client):
    response = client.get("/")
    assert response.status_code == 200
    data = response.json()
    assert data["message"] == "SMS Analysis Platform API"
    assert data["version"] == "1.0.0"

def test_health_endpoint(client):
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "healthy"}

def test_create_sms(client):
    sms_data = {
        "phone_number": "13800138000",
        "message_content": "您的验证码是123456，请在5分钟内使用。",
        "message_type": "received"
    }
    
    response = client.post("/api/v1/sms", json=sms_data)
    assert response.status_code == 200
    
    data = response.json()
    assert data["phone_number"] == sms_data["phone_number"]
    assert data["message_content"] == sms_data["message_content"]
    assert data["id"] is not None
    assert data["sentiment_score"] is not None
    assert data["category"] == "验证码"

def test_create_sms_batch(client):
    batch_data = {
        "sms_list": [
            {
                "phone_number": "13800138001",
                "message_content": "感谢您的购买！",
                "message_type": "received"
            },
            {
                "phone_number": "13800138002",
                "message_content": "您的订单已发货",
                "message_type": "received"
            }
        ]
    }
    
    response = client.post("/api/v1/sms/batch", json=batch_data)
    assert response.status_code == 200
    
    data = response.json()
    assert len(data) == 2
    assert data[0]["phone_number"] == "13800138001"
    assert data[1]["phone_number"] == "13800138002"

def test_get_sms_list(client):
    # First create some SMS records
    sms_data = {
        "phone_number": "13800138000",
        "message_content": "测试短信",
        "message_type": "received"
    }
    client.post("/api/v1/sms", json=sms_data)
    
    # Then get the list
    response = client.get("/api/v1/sms")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    assert len(data) > 0

def test_get_sms_by_id(client):
    # Create an SMS record
    sms_data = {
        "phone_number": "13800138000",
        "message_content": "测试短信",
        "message_type": "received"
    }
    create_response = client.post("/api/v1/sms", json=sms_data)
    sms_id = create_response.json()["id"]
    
    # Get the SMS by ID
    response = client.get(f"/api/v1/sms/{sms_id}")
    assert response.status_code == 200
    
    data = response.json()
    assert data["id"] == sms_id
    assert data["phone_number"] == sms_data["phone_number"]

def test_get_sms_by_phone(client):
    phone_number = "13800138999"
    sms_data = {
        "phone_number": phone_number,
        "message_content": "测试短信",
        "message_type": "received"
    }
    client.post("/api/v1/sms", json=sms_data)
    
    # Get SMS by phone number
    response = client.get(f"/api/v1/sms/phone/{phone_number}")
    assert response.status_code == 200
    
    data = response.json()
    assert isinstance(data, list)
    assert len(data) > 0
    assert data[0]["phone_number"] == phone_number

def test_analysis_stats(client):
    # Create some SMS records first
    sms_data_list = [
        {
            "phone_number": "13800138000",
            "message_content": "感谢您的购买！",
            "message_type": "received"
        },
        {
            "phone_number": "13800138001",
            "message_content": "您的验证码是123456",
            "message_type": "received"
        }
    ]
    
    for sms_data in sms_data_list:
        client.post("/api/v1/sms", json=sms_data)
    
    # Get analysis stats
    response = client.get("/api/v1/analysis/stats")
    assert response.status_code == 200
    
    data = response.json()
    assert "total_messages" in data
    assert "sentiment_distribution" in data
    assert "top_keywords" in data
    assert "category_distribution" in data
    assert "average_length" in data
    
    assert data["total_messages"] > 0

def test_delete_sms(client):
    # Create an SMS record
    sms_data = {
        "phone_number": "13800138000",
        "message_content": "测试短信",
        "message_type": "received"
    }
    create_response = client.post("/api/v1/sms", json=sms_data)
    sms_id = create_response.json()["id"]
    
    # Delete the SMS
    response = client.delete(f"/api/v1/sms/{sms_id}")
    assert response.status_code == 200
    assert "删除" in response.json()["message"]
    
    # Verify it's deleted
    get_response = client.get(f"/api/v1/sms/{sms_id}")
    assert get_response.status_code == 404