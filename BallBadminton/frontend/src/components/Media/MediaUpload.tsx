import React, { useState } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { mediaService, MediaItem } from '../../services/mediaService';
import Button from '../UI/Button';
import Modal from '../UI/Modal';

const UploadContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[4]};
`;

const DropZone = styled.div<{ isDragging: boolean }>`
  border: 2px dashed ${({ theme, isDragging }) => 
    isDragging ? theme.colors.primary[500] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.lg};
  padding: ${({ theme }) => theme.spacing[8]};
  text-align: center;
  background-color: ${({ theme, isDragging }) => 
    isDragging ? theme.colors.primary[50] : theme.colors.gray[50]};
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    border-color: ${({ theme }) => theme.colors.primary[500]};
    background-color: ${({ theme }) => theme.colors.primary[50]};
  }
`;

const DropZoneIcon = styled.div`
  font-size: ${({ theme }) => theme.fontSizes['4xl']};
  margin-bottom: ${({ theme }) => theme.spacing[3]};
`;

const DropZoneText = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.base};
  color: ${({ theme }) => theme.colors.gray[600]};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const DropZoneHint = styled.p`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[500]};
`;

const FileInput = styled.input`
  display: none;
`;

const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[2]};
`;

const Label = styled.label`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
`;

const Input = styled.input`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
  }
`;

const TextArea = styled.textarea`
  padding: ${({ theme }) => theme.spacing[2]} ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.base};
  min-height: 100px;
  resize: vertical;
  
  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.colors.primary[500]};
  }
`;

const PreviewContainer = styled.div`
  display: flex;
  flex-direction: column;
  gap: ${({ theme }) => theme.spacing[3]};
`;

const PreviewImage = styled.img`
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: ${({ theme }) => theme.borderRadius.md};
`;

const FileInfo = styled.div`
  padding: ${({ theme }) => theme.spacing[3]};
  background-color: ${({ theme }) => theme.colors.gray[50]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[600]};
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: ${({ theme }) => theme.spacing[3]};
  justify-content: flex-end;
`;

const ErrorMessage = styled.div`
  padding: ${({ theme }) => theme.spacing[3]};
  background-color: ${({ theme }) => theme.colors.error[50]};
  border: 1px solid ${({ theme }) => theme.colors.error[200]};
  border-radius: ${({ theme }) => theme.borderRadius.md};
  color: ${({ theme }) => theme.colors.error[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
`;

interface MediaUploadProps {
  galleryId: string;
  onUploadComplete?: (item: MediaItem) => void;
}

const MediaUpload: React.FC<MediaUploadProps> = ({ galleryId, onUploadComplete }) => {
  const { t } = useTranslation();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    tags: '',
  });

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setIsDragging(false);
    
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleFileSelect = (file: File) => {
    setError(null);
    
    // Validate file type
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'video/mp4', 'video/webm'];
    if (!validTypes.includes(file.type)) {
      setError(t('media.invalidFileType'));
      return;
    }
    
    // Validate file size (max 50MB)
    if (file.size > 50 * 1024 * 1024) {
      setError(t('media.fileTooLarge'));
      return;
    }
    
    setSelectedFile(file);
    setFormData({ ...formData, title: file.name.split('.')[0] });
    
    // Create preview for images
    if (file.type.startsWith('image/')) {
      const reader = new FileReader();
      reader.onload = (e) => {
        setPreviewUrl(e.target?.result as string);
      };
      reader.readAsDataURL(file);
    } else {
      setPreviewUrl(null);
    }
    
    setIsModalOpen(true);
  };

  const handleUpload = async () => {
    if (!selectedFile) return;
    
    setIsUploading(true);
    setError(null);
    
    try {
      const metadata: Partial<MediaItem> = {
        title: formData.title,
        description: formData.description,
        tags: formData.tags.split(',').map(tag => tag.trim()).filter(tag => tag),
        type: selectedFile.type.startsWith('image/') ? 'image' : 'video',
      };
      
      const uploadedItem = await mediaService.uploadMediaItem(galleryId, selectedFile, metadata);
      
      if (onUploadComplete) {
        onUploadComplete(uploadedItem);
      }
      
      handleClose();
    } catch (err) {
      setError(t('media.uploadFailed'));
      console.error('Upload failed:', err);
    } finally {
      setIsUploading(false);
    }
  };

  const handleClose = () => {
    setIsModalOpen(false);
    setSelectedFile(null);
    setPreviewUrl(null);
    setFormData({ title: '', description: '', tags: '' });
    setError(null);
  };

  return (
    <>
      <Button onClick={() => document.getElementById('file-input')?.click()}>
        {t('media.uploadMedia')}
      </Button>
      
      <FileInput
        id="file-input"
        type="file"
        accept="image/*,video/*"
        onChange={handleFileInputChange}
      />

      <Modal
        isOpen={isModalOpen}
        onClose={handleClose}
        title={t('media.uploadMedia')}
      >
        <UploadContainer>
          {!selectedFile ? (
            <DropZone
              isDragging={isDragging}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
              onClick={() => document.getElementById('file-input')?.click()}
            >
              <DropZoneIcon>📤</DropZoneIcon>
              <DropZoneText>{t('media.dropZoneText')}</DropZoneText>
              <DropZoneHint>{t('media.dropZoneHint')}</DropZoneHint>
            </DropZone>
          ) : (
            <>
              {error && <ErrorMessage>{error}</ErrorMessage>}
              
              <PreviewContainer>
                {previewUrl && <PreviewImage src={previewUrl} alt="Preview" />}
                <FileInfo>
                  <div><strong>{t('media.fileName')}:</strong> {selectedFile.name}</div>
                  <div><strong>{t('media.fileSize')}:</strong> {(selectedFile.size / 1024 / 1024).toFixed(2)} MB</div>
                  <div><strong>{t('media.fileType')}:</strong> {selectedFile.type}</div>
                </FileInfo>
              </PreviewContainer>

              <FormGroup>
                <Label>{t('media.title')}</Label>
                <Input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder={t('media.titlePlaceholder')}
                />
              </FormGroup>

              <FormGroup>
                <Label>{t('media.description')}</Label>
                <TextArea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder={t('media.descriptionPlaceholder')}
                />
              </FormGroup>

              <FormGroup>
                <Label>{t('media.tags')}</Label>
                <Input
                  type="text"
                  value={formData.tags}
                  onChange={(e) => setFormData({ ...formData, tags: e.target.value })}
                  placeholder={t('media.tagsPlaceholder')}
                />
              </FormGroup>

              <ButtonGroup>
                <Button variant="secondary" onClick={handleClose}>
                  {t('common.cancel')}
                </Button>
                <Button onClick={handleUpload} disabled={isUploading || !formData.title}>
                  {isUploading ? t('media.uploading') : t('media.upload')}
                </Button>
              </ButtonGroup>
            </>
          )}
        </UploadContainer>
      </Modal>
    </>
  );
};

export default MediaUpload;
