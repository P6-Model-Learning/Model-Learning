from Parser import reader
from Parser.util import enums
import argparse

parser = argparse.ArgumentParser(description="Print JSON from parsed syslog data")

parser.add_argument('-p','--prune', dest='prune', help='prune the data to remove trivial sequential entries', action="store_true")
parser.add_argument('-i', '--init', dest='init', help='parse initial boot data for boards', action='store_true')
parser.add_argument('-o', '--out', dest='out', help='rename the output, board name goes after -o')
parser.add_argument('-s','--simplify', dest='simplify', help='simplify the JSON object to only contain necessary keys', action='store_true')
parser.add_argument('-b', '--board', dest='board', help='only parse for one board type, add board name after -b/--board')
parser.add_argument('--print', dest='printboards', help='prints all boards in data', action='store_true')
parser.add_argument('--dir', dest='directory', help='directory to be traversed for data')

args = parser.parse_args()

journal = reader.Reader()

boards = [e.value for e in enums.Boards]

#check if non-parsing args
if args.printboards:
    print('Boards in data:')
    for i in boards:
        print('- ' + i.name.lower())
    quit()

if args.board and args.board in enums.Boards.__members__:
    boards = [args.board]
elif args.board:
    raise Exception("Something went wrong")

data = journal.parseBoards(boards, args.simplify, args.init, args.directory)

if args.out != None:
    with open('{args}.json'.format(args=args.out), 'w') as outfile:
        outfile.write(journal.parseToJSON(data))
else:
    with open('out.json', 'w') as outfile:
        outfile.write(journal.parseToJSON(data))