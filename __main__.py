from Parser import reader, pruner
import json
import argparse

parser = argparse.ArgumentParser(description="Print JSON from parsed syslog data")

parser.add_argument('-p','--prune', dest='prune', help='prune the data to remove trivial sequential entries', action="store_true")

args = parser.parse_args()

read = reader.Reader()
pruner = pruner.Pruner()

boards = read.getBoards()

# TODO SIGURD: Der skal obviously lige laves lidt om i logikken her
# når vi skal bruge alle boards i data
d = []
d.append(read.parseData(boards[0]))

if args.prune:
    pruner.badKTail(d)

if args.prune:
    pruner.badKTail(d)

dJSON = json.dumps(d, indent=2, sort_keys=True, default=str)

with open("out.json", "w") as outfile:
    outfile.write(dJSON)
