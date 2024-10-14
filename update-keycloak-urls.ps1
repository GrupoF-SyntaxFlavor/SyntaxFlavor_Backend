# Obtener la IP de la interfaz Wi-Fi  
$wifiAdapter = Get-NetAdapter | Where-Object { $_.Status -eq 'Up' -and $_.Name -like '*Wi-Fi*' }  
if ($wifiAdapter -eq $null) {  
    Write-Host "No se encontró ningún adaptador Wi-Fi activo."  
    exit  
}  

$myIP = (Get-NetIPAddress -InterfaceAlias $wifiAdapter.Name -AddressFamily IPv4).IPAddress  

# Imprimir la IP para depuración  
if (-not $myIP) {  
    Write-Host "No se pudo obtener la IP de la interfaz Wi-Fi."  
    exit  
}  

Write-Host "La IP obtenida es: $myIP"  

# Define las variables KEYCLOAK_HOST y KEYCLOAK_IP  
$keycloakHost = "http://${myIP}:8080"  
$keycloakIP = "http://${myIP}:8082" 

# Imprimir las URLs para depuración  
Write-Host "KEYCLOAK_HOST: $keycloakHost"  
Write-Host "KEYCLOAK_IP: $keycloakIP"  

# Leer el contenido del archivo  
$jsonFilePath = "src\main\resources\imports\keycloak-realm\realm-export.json"  

# Comprobar si el archivo existe  
if (-Not (Test-Path $jsonFilePath)) {  
    Write-Host "El archivo no existe: $jsonFilePath"  
    exit  
}  

$content = Get-Content $jsonFilePath -Raw  

# Actualiza el archivo realm-export.json con las nuevas direcciones IP  
$content = $content -replace '\${KEYCLOAK_HOST}', $keycloakHost  
$content = $content -replace '\${KEYCLOAK_IP}', $keycloakIP  

# Guarda el contenido actualizado de nuevo en el archivo  
Set-Content -Path $jsonFilePath -Value $content -Force  

Write-Host "Las URLs en realm-export.json han sido actualizadas con la IP $myIP."

