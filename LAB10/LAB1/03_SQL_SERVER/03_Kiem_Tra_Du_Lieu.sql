-- Kiểm tra dữ liệu lịch sử thao tác
-- Sử dụng ATBMHTTT_Lab10 hoặc LAB1_MaHoaCoDien tùy cấu hình

-- Kiểm tra trên ATBMHTTT_Lab10:
IF DB_ID(N'ATBMHTTT_Lab10') IS NOT NULL
BEGIN
    USE [ATBMHTTT_Lab10];
    PRINT '=== DỮ LIỆU TẠI ATBMHTTT_Lab10 ===';
    SELECT TOP (20) id, created_at, algorithm, operation, input_text, result_text 
    FROM dbo.history ORDER BY id DESC; 

    SELECT COUNT(*) AS so_ban_ghi FROM dbo.history; 

    SELECT algorithm, COUNT(*) AS so_lan 
    FROM dbo.history GROUP BY algorithm ORDER BY so_lan DESC; 
END;
GO

-- Kiểm tra trên LAB1_MaHoaCoDien:
IF DB_ID(N'LAB1_MaHoaCoDien') IS NOT NULL
BEGIN
    USE [LAB1_MaHoaCoDien]; 
    PRINT '=== DỮ LIỆU TẠI LAB1_MaHoaCoDien ===';
    SELECT TOP (20) id, created_at, algorithm, operation, input_text, result_text 
    FROM dbo.history ORDER BY id DESC; 

    SELECT COUNT(*) AS so_ban_ghi FROM dbo.history; 

    SELECT algorithm, COUNT(*) AS so_lan 
    FROM dbo.history GROUP BY algorithm ORDER BY so_lan DESC; 
END;
GO
