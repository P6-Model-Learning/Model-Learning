from Parser import reader, pruner, journalParser
import argparse

parser = argparse.ArgumentParser(description="Print JSON from parsed syslog data")

parser.add_argument('-p','--prune', dest='prune', help='prune the data to remove trivial sequential entries', action="store_true")
parser.add_argument('-i', '--init', dest='init', help='parse initial boot data for boards', action='store_true')
parser.add_argument('-o', '--out', dest='out', help='output the json file at current working directory', action='store_true')

args = parser.parse_args()

journal = None
prune = None

#check for 'data type' wether init data or new data
if args.init:
    journal = reader.Reader()
else:
    journal = journalParser.JournalParser()


boards = journal.getBoards()
data = journal.parse()

if args.prune:
    prune = pruner.Pruner()
    prune.badKTail()

if args.out:
    with open('out.json', 'w') as outfile:
        outfile.write(journal.parseToJSON(data))
