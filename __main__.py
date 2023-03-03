from Parser import reader, pruner
import json

read = reader.Reader()
pruner = pruner.Pruner()

boards = read.getBoards()

d = read.parseData(boards[0])
# pruner.prune(d)

dJSON = json.dumps(d, indent=2, sort_keys=True, default=str)

with open("out.json", "w") as outfile:
    outfile.write(dJSON)
