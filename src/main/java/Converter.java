import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.jcodec.api.specific.AVCMP4Adaptor;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.AutoFileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.common.model.Size;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.demuxer.AbstractMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.FramesMP4DemuxerTrack;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ann
 * Date: 10/27/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class Converter {

    public static void convert(String outputFile, String inputFile){
        try {
            MP4Demuxer demuxer = new MP4Demuxer(new AutoFileChannelWrapper( new File(inputFile)));
            MP4Muxer muxer = new MP4Muxer(NIOUtils.rwFileChannel(new File(outputFile)));


            List<AbstractMP4DemuxerTrack> audioTracks = demuxer.getAudioTracks();
            for (AbstractMP4DemuxerTrack audioTrack : audioTracks) {
                FramesMP4MuxerTrack muxerTrack = muxer.addTrack(TrackType.SOUND, (int) audioTrack.getTimescale());
                muxerTrack.addSampleEntry(audioTrack.getSampleEntries()[0]);
                for (int i = 0; i < audioTrack.getFrameCount(); i++) {
                    muxerTrack.addFrame((MP4Packet) audioTrack.nextFrame());
                }
            }

            FramesMP4DemuxerTrack videoTrack = (FramesMP4DemuxerTrack) demuxer.getVideoTrack();
            FramesMP4MuxerTrack muxerTrack = muxer.addTrack(TrackType.VIDEO, (int) videoTrack.getTimescale());

            AVCMP4Adaptor decoder = new AVCMP4Adaptor(videoTrack);
            H264Encoder encoder = new H264Encoder();

            Size size = videoTrack.getMeta().getDimensions();
            long frameCount = videoTrack.getFrameCount();
            List<ByteBuffer> spsList = new ArrayList<ByteBuffer>();
            List<ByteBuffer> ppsList = new ArrayList<ByteBuffer>();

            ByteBuffer outBuffer = ByteBuffer.allocate(size.getWidth() * size.getHeight());
            for (int i = 0; i < frameCount; i++) {
                //updateProgress(i, frameCount);

                if (Thread.currentThread().isInterrupted()){
                    return;
                }

                MP4Packet packet = videoTrack.nextFrame();
                Picture picture;
                try {
                    picture = decoder.decodeFrame(packet, decoder.allocatePicture());
                } catch (Exception ex) {
                    continue;
                }
                BufferedImage srcImage = ConverterUtils.toBufferedImage(picture);



                BufferedImage precessedImage = core.getCombinedImage();
                Picture modifiedPicture = ConverterUtils.fromBufferedImage(precessedImage, ColorSpace.YUV420J);

                outBuffer.clear();
                outBuffer = encoder.encodeFrame(modifiedPicture, outBuffer);

                spsList.clear();
                ppsList.clear();
                H264Utils.wipePS(outBuffer, spsList, ppsList);
                H264Utils.encodeMOVPacket(outBuffer);

                MP4Packet modifiedPacket = new MP4Packet(packet, outBuffer);
                muxerTrack.addFrame(modifiedPacket);
            }

            muxerTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));

            muxer.writeHeader();
        } catch (Exception e) {
            if (e instanceof ClosedByInterruptException) {
                core.setConvertThreadException(new Exception("Interrupted"));
                return;
            }
            core.setConvertThreadException(e);
        } finally {
            releaseButton();
        }
    }
    private void setWaterMark(String imageName, String newImageName) throws IOException {
        BufferedImage waterMark = ImageIO.read(new File("src/image/minion.jpg"));
        waterMark = Thumbnails.of(waterMark).size(100,100).asBufferedImage();
        BufferedImage image = ImageIO.read(new File(imageName));
        Thumbnails.of(image)
                .scale(1)
                .watermark(Positions.BOTTOM_RIGHT, waterMark, 0.25f)
                .toFile(new File(newImageName));



    }
    }
}

