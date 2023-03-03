from Parser import reader
import json

read = reader.Reader()

r = read.reader()

d = read.parseData(r[0])

dJSON = json.dumps(d, indent=2, sort_keys=True, default=str)

with open("out.json", "w") as outfile:
    outfile.write(dJSON)
