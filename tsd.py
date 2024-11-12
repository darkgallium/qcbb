import requests
from time import sleep
from pathlib import Path
from random import randint

# telecom standards downloader

LISTING_URL = "https://www.etsi.org/?option=com_standardssearch&view=data&format=json&search=&title=1&etsiNumber=1&content=0&version=0&onApproval=1&published=1&withdrawn=1&historical=1&isCurrent=1&superseded=0&startDate=1988-01-15&endDate=2024-11-11&harmonized=0&keyword=LTE,,LTE-Advanced&TB=372,,376,,377,,378,,513,,496,,439,,649,,651,,653,,654,,655,,656,,535,,536,,537,,538,,622,,539,,575,,373,,379,,380,,3&stdType=TS&frequency=&mandate=&collection=&sort=1"

STORAGE_PATH = Path('./pdf')
STORAGE_PATH.mkdir(exist_ok=True)

url_map = {}
ver_map = {}

def make_listing_request(page: int):
    params = {
        'page': page
    }

    r = requests.get(LISTING_URL, params=params)
    return r.json()


def make_download(url: str):
    print(url)
    r = requests.get(url)
    path = STORAGE_PATH / url.split('/')[-1]
    path.open('wb').write(r.content)

def is_candidate(item):
    if "Radio layer 1" in item["TB"]:
        return False

    ref = item["WKI_REFERENCE"].split("v") # format: RTS/TSGR-<spec1 3 digits 0prefixed><spec2 3 digits 0prefixed>v<alpha rev><hexa rev>
    if ref[0] in ver_map and ref[1][0] <= ver_map[ref[0]]:
        return False

    return True 


for i in range(1,100):

    payload = make_listing_request(i)
    
    for item in payload:
        if is_candidate(item):
            #print(item["ETSI_DELIVERABLE"])
            ref = item["WKI_REFERENCE"].split("v")
            url = f'https://www.etsi.org/deliver/{item["EDSpathname"]}{item["EDSPDFfilename"]}'
            path = STORAGE_PATH / item["EDSPDFfilename"]

            #if path.exists():
            #    print("isok")
            #    continue
            
            ver_map[ref[0]] = ref[1][0]
            url_map[ref[0]] = url
            #print(item["ETSI_DELIVERABLE"])
    
    print(list(url_map.keys()))
    print("sleeping")
    sleep(randint(1,4))

for url in url_map.values():
    make_download(url)
    sleep(randint(1,4))
