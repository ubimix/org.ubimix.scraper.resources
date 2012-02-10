/**
 * 
 */
package org.webreformatter.resources.adapters.images;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.webreformatter.commons.uri.Path;
import org.webreformatter.resources.IContentAdapter;
import org.webreformatter.resources.IContentAdapter.ContentChangeEvent;
import org.webreformatter.resources.IWrfResource;
import org.webreformatter.resources.WrfResourceAdapter;
import org.webreformatter.resources.adapters.images.ImageResizeStrategy.Format;
import org.webreformatter.resources.adapters.mime.MimeTypeAdapter;

/**
 * This adapter is used to represent a resource as a string.
 * 
 * @author kotelnikov
 */
public class ImageAdapter extends WrfResourceAdapter {

    private static ImageResizer fResizer = new ImageResizer();

    private BufferedImage fImage;

    public ImageAdapter(IWrfResource instance) {
        super(instance);
    }

    public BufferedImage getImage() throws IOException {
        synchronized (getMutex()) {
            if (fImage == null) {
                IContentAdapter contentAdapter = fResource
                    .getAdapter(IContentAdapter.class);
                if (contentAdapter.exists()) {
                    InputStream input = contentAdapter.getContentInput();
                    try {
                        fImage = ImageResizer.readImage(input);
                    } finally {
                        input.close();
                    }
                }
            }
            return fImage;
        }
    }

    /**
     * Detects and returns a file extension for this image.
     * 
     * @return the file extension for this image
     * @throws IOException
     */
    public String getImageFileExtension() throws IOException {
        MimeTypeAdapter mimeAdapter = fResource
            .getAdapter(MimeTypeAdapter.class);
        String mimeType = mimeAdapter.getMimeType();
        String ext = "jpg";
        if ("image/jpeg".equals(mimeType) || "image/jpg".equals(mimeType)) {
            ext = "jpg";
        } else if ("image/png".equals(mimeType)) {
            ext = "png";
        } else if ("image/gif".equals(mimeType)) {
            ext = "gif";
        }
        return ext;
    }

    public Point getImageSize() throws IOException {
        BufferedImage image = getImage();
        Point result = image != null ? ImageResizer.getImageSize(image) : null;
        return result;
    }

    protected Object getMutex() {
        return this;
    }

    @Override
    public void handleEvent(Object event) {
        synchronized (getMutex()) {
            if (event instanceof ContentChangeEvent) {
                fImage = null;
            }
        }
    }

    public void resizeImage(
        ImageAdapter target,
        ImageResizeStrategy resizeStrategy) throws IOException {
        BufferedImage image = getImage();
        BufferedImage resizedImage = fResizer
            .resizeImage(image, resizeStrategy);
        Format format = resizeStrategy.getNewImageFormat();
        target.setImage(resizedImage, format);
    }

    public void resizeImage(
        IWrfResource target,
        ImageResizeStrategy resizeStrategy) throws IOException {
        ImageAdapter targetImageAdapter = target.getAdapter(ImageAdapter.class);
        resizeImage(targetImageAdapter, resizeStrategy);
    }

    /**
     * This method resizes this image to the specified boundaries.
     * 
     * @param width the width of the image
     * @param height the height of the image
     * @param exactDimension if this flag is <code>true</code> then the
     *        resulting image will fit to the specified dimensions; otherwise it
     *        will be resized proportionally only if it is bigger than the
     *        specified dimensions
     * @param format the format of the requested resized image
     * @return a reduced copy of this image if it does not fit in the specified
     *         box; otherwise the reference to this adapter is returned
     * @throws IOException
     */
    public ImageAdapter resizeTo(
        final int width,
        final int height,
        final boolean exactDimension,
        Format format) throws IOException {
        Point imageSize = getImageSize();
        ImageAdapter result = this;
        if (imageSize == null) {
            result = null;
        } else {
            boolean resize = exactDimension
                || (imageSize.x > width || imageSize.y > height);
            // FIXME: check that the format of this resource.
            // If an another format is required then the "resize" property
            // should also be "true".
            if (resize) {
                Path path = fResource.getPath();
                path = path
                    .getBuilder()
                    .appendPath(width + "x" + height)
                    .build();
                IWrfResource resizedImage = fResource
                    .getProvider()
                    .getResource(path, true);
                ImageAdapter resizedImageAdapter = resizedImage
                    .getAdapter(ImageAdapter.class);
                resizeImage(
                    resizedImageAdapter,
                    new ImageResizeStrategy(format) {
                        @Override
                        public Point getImageSize(Point originalImageSize) {
                            return exactDimension
                                ? new Point(width, height)
                                : notMoreThan(originalImageSize, width, height);
                        }
                    });
                result = resizedImageAdapter;
            }
        }
        return result;

    }

    /**
     * This method resizes this image to the specified boundaries.
     * 
     * @param width the width of the image
     * @param height the height of the image
     * @param exactDimension if this flag is <code>true</code> then the
     *        resulting image will fit to the specified dimensions; otherwise it
     *        will be resized proportionally only if it is bigger than the
     *        specified dimensions
     * @param format the format of the requested resized image
     * @return a reduced copy of this image if it does not fit in the specified
     *         box; otherwise the reference to this adapter is returned
     * @throws IOException
     */
    public IWrfResource resizeToImage(
        final int width,
        final int height,
        final boolean exactDimension,
        Format format) throws IOException {
        ImageAdapter copy = resizeTo(width, height, exactDimension, format);
        return copy != null ? copy.getResource() : null;
    }

    public void setImage(BufferedImage resizedImage) throws IOException {
        setImage(resizedImage, ImageResizeStrategy.Format.JPG);
    }

    public void setImage(
        BufferedImage resizedImage,
        ImageResizeStrategy.Format format) throws IOException {
        IContentAdapter contentAdapter = fResource
            .getAdapter(IContentAdapter.class);
        OutputStream output = contentAdapter.getContentOutput();
        try {
            ImageResizer.writeImage(resizedImage, output, format);
        } finally {
            output.close();
        }
        synchronized (getMutex()) {
            fImage = resizedImage;
        }
    }

}
