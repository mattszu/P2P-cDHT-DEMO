# P2P-cDHT-DEMO
[demo protocol of a Peer to Peer circular Distributed Hash Table - on Linux]

*my first foray in java network porgramming and use of threads

Peer circularly connected, each peer is assigned a number, and is 'connected' to and pings the next two peers and after it.
i.e in a peer list of 1,3,4,5,8,10,12,15   peer 1 pings 3&4, peer 3 pings 4&5, .. peer 15 pings 1&3.

getting started - execute starting script in terminal:

xterm -hold -title "Peer 1" -e "java cdht 1 3 4" &
xterm -hold -title "Peer 3" -e "java cdht 3 4 5" &
xterm -hold -title "Peer 4" -e "java cdht 4 5 8" &
xterm -hold -title "Peer 5" -e "java cdht 5 8 10" &
xterm -hold -title "Peer 8" -e "java cdht 8 10 12" &
xterm -hold -title "Peer 10" -e "java cdht 10 12 15" &
xterm -hold -title "Peer 12" -e "java cdht 12 15 1" &
xterm -hold -title "Peer 15" -e "java cdht 15 1 3" &

This executes multiple instances of the peer in multiple terminals all with correct peer pairings.
Once established peers will begin to ping each other, this can be seen as peers will report when
sending and recieving pings.

From here selecting a peer, commands can be typed into the corresponding terminal.

COMMANDS

request: request a file - file must be a four digit number e.g "request 2012"

quit: gracefully depart from peer network

files undergo simulated hashing i.e filename converted to integers and divided by 256.
the hash number is between 0-255.

if the hash value is n, the file is stored in successor of n. values of three different files are 6, 10 and 210,
then they will be stored, respectively, in peers 8, 10 and 1.

this obviously changes as peer enter and leave the network.

for more details read the designdoc.txt for information on operations and limitations.
- this demo deals with peer exit but not peer entry.
