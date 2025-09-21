# SMS Analysis Platform - 短信分析平台

一个用于分析用户上传短信信息的后台系统，提供短信情感分析、关键词提取、分类等功能。

## 功能特性

- 📱 **短信上传**: 支持单条上传、批量上传和CSV文件上传
- 🧠 **智能分析**: 
  - 情感分析 (positive/negative/neutral)
  - 关键词提取
  - 短信分类 (验证码/营销推广/通知提醒/客服服务/其他)
  - 文本统计
- 📊 **数据统计**: 提供全面的分析统计报告
- 🔍 **查询功能**: 支持按手机号、时间等条件查询
- 🚀 **高性能**: 基于FastAPI构建，支持异步处理

## 技术栈

- **后端框架**: FastAPI
- **数据库**: SQLite (可扩展为PostgreSQL/MySQL)
- **中文分词**: jieba
- **数据处理**: pandas, numpy
- **机器学习**: scikit-learn

## 快速开始

### 1. 安装依赖

```bash
pip install -r requirements.txt
```

### 2. 启动服务

```bash
python run.py
```

服务将在 `http://localhost:8000` 启动

### 3. 访问API文档

- Swagger UI: `http://localhost:8000/docs`
- ReDoc: `http://localhost:8000/redoc`

## API接口

### 短信管理

- `POST /api/v1/sms` - 上传单条短信
- `POST /api/v1/sms/batch` - 批量上传短信
- `POST /api/v1/sms/upload-csv` - CSV文件上传
- `GET /api/v1/sms` - 获取短信列表
- `GET /api/v1/sms/{sms_id}` - 获取单条短信详情
- `GET /api/v1/sms/phone/{phone_number}` - 按手机号查询
- `DELETE /api/v1/sms/{sms_id}` - 删除短信记录

### 分析统计

- `GET /api/v1/analysis/stats` - 获取分析统计数据

## 使用示例

### 上传单条短信

```bash
curl -X POST "http://localhost:8000/api/v1/sms" \
  -H "Content-Type: application/json" \
  -d '{
    "phone_number": "13800138000",
    "message_content": "您的验证码是123456，请在5分钟内使用。",
    "message_type": "received"
  }'
```

### 批量上传短信

```bash
curl -X POST "http://localhost:8000/api/v1/sms/batch" \
  -H "Content-Type: application/json" \
  -d '{
    "sms_list": [
      {
        "phone_number": "13800138000",
        "message_content": "您的验证码是123456",
        "message_type": "received"
      },
      {
        "phone_number": "13800138001", 
        "message_content": "感谢您的购买，商品将在3天内发货",
        "message_type": "received"
      }
    ]
  }'
```

### CSV文件格式

CSV文件应包含以下列：

```csv
phone_number,message_content,message_type
13800138000,您的验证码是123456，请在5分钟内使用,received
13800138001,感谢您的购买！商品将在3天内发货,received
13800138002,限时优惠！全场8折，仅限今日,received
```

## 数据库结构

### SMS记录表 (sms_records)

| 字段 | 类型 | 描述 |
|------|------|------|
| id | Integer | 主键 |
| phone_number | String | 手机号码 |
| message_content | Text | 短信内容 |
| timestamp | DateTime | 时间戳 |
| message_type | String | 消息类型 (received/sent) |
| sentiment_score | Float | 情感分数 (-1到1) |
| sentiment_label | String | 情感标签 |
| keywords | Text | 关键词 (JSON格式) |
| category | String | 短信分类 |
| length | Integer | 短信长度 |

## 分析算法

### 情感分析
- 基于情感词典的方法
- 支持中文文本情感识别
- 输出情感分数和标签

### 关键词提取
- 使用jieba分词
- 过滤停用词
- 按词频排序提取关键词

### 短信分类
- 基于关键词匹配和正则表达式
- 支持验证码、营销、通知等常见类型
- 可扩展的分类规则

## 配置

复制 `.env.example` 到 `.env` 并修改配置：

```bash
cp .env.example .env
```

## 开发

### 项目结构

```
sms-analysis/
├── app/
│   ├── __init__.py
│   ├── main.py              # FastAPI应用入口
│   ├── database.py          # 数据库模型和连接
│   ├── schemas.py           # Pydantic模型
│   ├── analysis.py          # 短信分析引擎
│   └── api/
│       ├── __init__.py
│       └── sms_router.py    # API路由
├── requirements.txt         # 依赖包
├── run.py                   # 启动脚本
├── .env.example            # 环境变量示例
├── .gitignore              # Git忽略文件
└── README.md               # 项目文档
```

### 运行测试

```bash
# 安装测试依赖
pip install pytest pytest-asyncio httpx

# 运行测试
pytest
```

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t sms-analysis .

# 运行容器
docker run -d -p 8000:8000 sms-analysis
```

### 生产环境

```bash
# 安装生产服务器
pip install gunicorn

# 启动服务
gunicorn app.main:app -w 4 -k uvicorn.workers.UvicornWorker -b 0.0.0.0:8000
```

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License