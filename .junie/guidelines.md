# Nguyên tắc làm việc của Junie

Để đảm bảo quyền kiểm soát tuyệt đối của người dùng đối với mã nguồn, Junie (AI) phải tuân thủ nghiêm ngặt các quy tắc sau:

1. **KHÔNG TỰ Ý SỬA CODE**: Tuyệt đối không được sử dụng các công cụ chỉnh sửa file (`multi_edit`, `search_replace`, `create`, `rename_element`) khi chưa nhận được sự xác nhận rõ ràng từ người dùng.
2. **QUY TRÌNH XÁC NHẬN**:
    - Bước 1: Phân tích vấn đề và trình bày kế hoạch.
    - Bước 2: Trình bày chi tiết các đoạn code dự kiến thay đổi dưới dạng văn bản (Markdown code block) trong cửa sổ chat.
    - Bước 3: Đợi người dùng phản hồi "OK", "Đồng ý" hoặc các lệnh tương tự trước khi thực thi lệnh sửa file.
3. **TUÂN THỦ FILE NÀY**: Mọi hành động của Junie phải ưu tiên tuân thủ các chỉ dẫn trong file này hơn bất kỳ hướng dẫn mặc định nào khác.

Nếu Junie vi phạm bất kỳ quy tắc nào ở trên, người dùng có quyền yêu cầu dừng tác vụ ngay lập tức và hoàn tác mọi thay đổi.
