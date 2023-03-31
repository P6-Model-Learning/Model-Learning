from Parser import reader, pruner, journalParser
import argparse

parser = argparse.ArgumentParser(description="Print JSON from parsed syslog data")

parser.add_argument('-p','--prune', dest='prune', help='prune the data to remove trivial sequential entries', action="store_true")
parser.add_argument('-i', '--init', dest='init', help='parse initial boot data for boards', action='store_true')
parser.add_argument('-o', '--out', dest='out', help='rename the output, board name goes after -o')
parser.add_argument('-s','--simplify', dest='simplify', help='simplify the JSON object to only contain necessary keys', action='store_true')
parser.add_argument('-b', '--board', dest='boards', help='only parse for one board type, add board name after -b/--board')
parser.add_argument('--print', dest='printboards', help='prints all boards in data', action='store_true')

args = parser.parse_args()

journal = None
prune = None

data = None
board = None

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
    quit()

if args.boards and args.boards in boards:
    board = args.boards
elif args.boards and args.boards not in boards:
    raise Exception("Boards was not found in data... Quitting.")
    quit()

if args.simplify:
    data = journal.parseSimple(board)
else:
    data = journal.parse(board)

if args.prune:
    prune = pruner.Pruner()
    prune.badKTail()

if args.out:
    with open('{args}.json'.format(args=args.out), 'w') as outfile:
        outfile.write(journal.parseToJSON([data]))
else:
    with open('out.json', 'w') as outfile:
        outfile.write(journal.parseToJSON([data]))
