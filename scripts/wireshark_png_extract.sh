# Given a TCP capture, this script allows you to get images from it
# Run it like ./wireshark_png_extract.sh [capture file]
# Thanks to Abdelouahab Laaroussi for his contribution


infile=$1

for stream in $(tshark -nlr $infile -Y tcp.flags.syn==1 -T fields -e tcp.stream | sort -n | uniq | sed 's/\r//')
do
    echo "Processing stream $stream: img_${stream}.png"
    tshark -nlr $infile -qz "follow,tcp,raw,$stream" | tail -n +7 | sed 's/^\s\+//g' | xxd -r -p > img_${stream}.png
done
