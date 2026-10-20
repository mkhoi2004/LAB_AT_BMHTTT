# Run on DC01 in an elevated PowerShell window.
$zone = "lab7.test"
$ip = "172.16.6.10"
$names = @("normal","china","mideast","jobs","weapons","politics","religion","education","entertainment","games","streaming","blockedgame","download")
if (-not (Get-DnsServerZone -Name $zone -ErrorAction SilentlyContinue)) {
    Add-DnsServerPrimaryZone -Name $zone -ReplicationScope "Domain"
}
foreach ($n in $names) {
    if (-not (Get-DnsServerResourceRecord -ZoneName $zone -Name $n -RRType A -ErrorAction SilentlyContinue)) {
        Add-DnsServerResourceRecordA -ZoneName $zone -Name $n -IPv4Address $ip
    }
}
Get-DnsServerResourceRecord -ZoneName $zone -RRType A
