from Parser import reader, pruner, journalParser
import argparse

parser = argparse.ArgumentParser(description="Print JSON from parsed syslog data")

parser.add_argument('-p','--prune', dest='prune', help='prune the data to remove trivial sequential entries', action="store_true")
parser.add_argument('-i', '--init', dest='init', help='parse initial boot data for boards', action='store_true')
parser.add_argument('-o', '--out', dest='out', help='output the json file at current working directory', action='store_true')
parser.add_argument('-s','--simplify', dest='simplify', help='simplify the JSON object to only contain necessary keys', action='store_true')
parser.add_argument('-b', '--board', dest='boards', help='only parse for one board type, add board name after -b/--board')
parser.add_argument('--print', dest='printboards', help='prints all boards in data', action='store_true')

args = parser.parse_args()

journal = None
prune = None
data = None

#check for 'data type' wether init data or new data
if args.init:
    journal = reader.Reader()
else:
    journal = journalParser.JournalParser()

boards = dict(journal.getBoards())

#check if non-parsing args
if args.printboards:
    print('Boards in data:')
    for i in boards:
        print(' * ', i)

if args.boards:
    print('Choose a board from the list:')
    for i in boards:
        print(' * ' + i)
    x = input()
    while x not in boards:
        print("board not in inputs check again")
        x  =input()
    print('yay')

if args.simplify:
    data = journal.parseSimple()
else:
    data = journal.parse()

if args.prune:
    prune = pruner.Pruner()
    prune.badKTail()

if args.out:
    with open('out.json', 'w') as outfile:
        outfile.write(journal.parseToJSON(data))