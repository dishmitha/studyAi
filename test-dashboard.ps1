# Test Dashboard Endpoint - Gets fresh token automatically
$loginResponse = Invoke-WebRequest -Uri 'http://localhost:8080/api/auth/login' -Method POST -ContentType 'application/json' -Body '{"email":"testuser@test.com","password":"password123"}' -UseBasicParsing | ConvertFrom-Json

$token = $loginResponse.token
$headers = @{"Authorization" = "Bearer $token"}

Write-Host "Token acquired successfully"
Write-Host "Testing Dashboard endpoint..."

Invoke-WebRequest -Uri 'http://localhost:8080/api/dashboard' -Method GET -Headers $headers -UseBasicParsing | Select-Object -ExpandProperty Content
