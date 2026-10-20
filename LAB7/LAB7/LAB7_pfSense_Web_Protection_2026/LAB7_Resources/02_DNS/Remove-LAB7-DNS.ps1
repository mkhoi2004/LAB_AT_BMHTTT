# Run on DC01 only when the LAB7 zone is no longer needed.
Remove-DnsServerZone -Name "lab7.test" -Force
