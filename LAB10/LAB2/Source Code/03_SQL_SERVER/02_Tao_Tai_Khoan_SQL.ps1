$ErrorActionPreference = 'Stop'

$server = Read-Host 'SQL Server [127.0.0.1,1433]'
if ([string]::IsNullOrWhiteSpace($server)) {
    $server = '127.0.0.1,1433'
}
if ($server.Contains(';')) {
    throw 'Server name must not contain a semicolon.'
}
$securePassword = Read-Host 'New password for SQL login lab2_user' -AsSecureString
if ($securePassword.Length -lt 8) {
    $securePassword.Dispose()
    throw 'SQL password must be at least 8 characters.'
}

$passwordPointer = [IntPtr]::Zero
$connection = $null
try {
    $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
    $plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $quotedPassword = $plainPassword.Replace("'", "''")

    $connectionString = [System.Data.SqlClient.SqlConnectionStringBuilder]::new(
        "Data Source=$server;Initial Catalog=master;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=5")
    $connection = [System.Data.SqlClient.SqlConnection]::new($connectionString.ConnectionString)
    $connection.Open()
    $command = $connection.CreateCommand()
    $command.CommandText = @"
IF SUSER_ID(N'lab2_user') IS NULL
    CREATE LOGIN [lab2_user] WITH PASSWORD = N'$quotedPassword', CHECK_POLICY = ON;
ELSE
    ALTER LOGIN [lab2_user] WITH PASSWORD = N'$quotedPassword', CHECK_POLICY = ON;
USE [LAB2_MaHoaHienDai];
IF DATABASE_PRINCIPAL_ID(N'lab2_user') IS NULL
    CREATE USER [lab2_user] FOR LOGIN [lab2_user];
GRANT SELECT, INSERT ON OBJECT::dbo.app_users TO [lab2_user];
GRANT SELECT, INSERT ON OBJECT::dbo.crypto_history TO [lab2_user];
"@
    [void]$command.ExecuteNonQuery()
    Write-Output "Configured SQL login lab2_user on $server."
}
finally {
    if ($connection -ne $null) {
        $connection.Dispose()
    }
    if ($passwordPointer -ne [IntPtr]::Zero) {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    }
    $securePassword.Dispose()
    if ($plainPassword) {
        $plainPassword = $null
    }
    if ($quotedPassword) {
        $quotedPassword = $null
    }
}
