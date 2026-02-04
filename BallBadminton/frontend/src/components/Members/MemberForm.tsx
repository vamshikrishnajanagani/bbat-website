import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useTranslation } from 'react-i18next';
import { Member } from '../../store/slices/membersSlice';
import { membersService, CreateMemberRequest, UpdateMemberRequest } from '../../services/membersService';
import Button from '../UI/Button';
import Modal from '../UI/Modal';

const FormContainer = styled.div`
  padding: ${({ theme }) => theme.spacing[6]};
`;

const FormGrid = styled.div`
  display: grid;
  gap: ${({ theme }) => theme.spacing[4]};
  
  @media (min-width: ${({ theme }) => theme.breakpoints.md}) {
    grid-template-columns: 1fr 1fr;
  }
`;

const FormGroup = styled.div<{ fullWidth?: boolean }>`
  ${({ fullWidth }) => fullWidth && 'grid-column: 1 / -1;'}
`;

const Label = styled.label`
  display: block;
  font-weight: ${({ theme }) => theme.fontWeights.medium};
  color: ${({ theme }) => theme.colors.gray[700]};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  margin-bottom: ${({ theme }) => theme.spacing[2]};
`;

const Input = styled.input<{ hasError?: boolean }>`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ hasError, theme }) => 
    hasError ? theme.colors.error[300] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ hasError, theme }) => 
      hasError ? theme.colors.error[500] : theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ hasError, theme }) => 
      hasError ? theme.colors.error[100] : theme.colors.primary[100]};
  }
  
  &::placeholder {
    color: ${({ theme }) => theme.colors.gray[400]};
  }
`;

const TextArea = styled.textarea<{ hasError?: boolean }>`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ hasError, theme }) => 
    hasError ? theme.colors.error[300] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ hasError, theme }) => 
      hasError ? theme.colors.error[500] : theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ hasError, theme }) => 
      hasError ? theme.colors.error[100] : theme.colors.primary[100]};
  }
  
  &::placeholder {
    color: ${({ theme }) => theme.colors.gray[400]};
  }
`;

const Select = styled.select<{ hasError?: boolean }>`
  width: 100%;
  padding: ${({ theme }) => theme.spacing[3]};
  border: 1px solid ${({ hasError, theme }) => 
    hasError ? theme.colors.error[300] : theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  font-size: ${({ theme }) => theme.fontSizes.sm};
  background: white;
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:focus {
    outline: none;
    border-color: ${({ hasError, theme }) => 
      hasError ? theme.colors.error[500] : theme.colors.primary[500]};
    box-shadow: 0 0 0 3px ${({ hasError, theme }) => 
      hasError ? theme.colors.error[100] : theme.colors.primary[100]};
  }
`;

const CheckboxGroup = styled.div`
  display: flex;
  align-items: center;
  gap: ${({ theme }) => theme.spacing[2]};
`;

const Checkbox = styled.input`
  width: 16px;
  height: 16px;
  accent-color: ${({ theme }) => theme.colors.primary[500]};
`;

const CheckboxLabel = styled.label`
  font-size: ${({ theme }) => theme.fontSizes.sm};
  color: ${({ theme }) => theme.colors.gray[700]};
  cursor: pointer;
`;

const ErrorMessage = styled.span`
  display: block;
  color: ${({ theme }) => theme.colors.error[600]};
  font-size: ${({ theme }) => theme.fontSizes.xs};
  margin-top: ${({ theme }) => theme.spacing[1]};
`;

const PhotoUploadSection = styled.div`
  border: 2px dashed ${({ theme }) => theme.colors.gray[300]};
  border-radius: ${({ theme }) => theme.borderRadius.base};
  padding: ${({ theme }) => theme.spacing[6]};
  text-align: center;
  transition: border-color ${({ theme }) => theme.transitions.fast};
  
  &:hover {
    border-color: ${({ theme }) => theme.colors.primary[400]};
  }
`;

const PhotoPreview = styled.div`
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: ${({ theme }) => theme.colors.gray[200]};
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto ${({ theme }) => theme.spacing[3]} auto;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`;

interface MemberFormData {
  name: string;
  position: string;
  email: string;
  phone: string;
  biography: string;
  photoUrl: string;
  hierarchyLevel: number;
  tenureStartDate: string;
  tenureEndDate: string;
  isActive: boolean;
  isProminent: boolean;
}

interface MemberFormErrors {
  [key: string]: string;
}

interface MemberFormProps {
  member?: Member;
  onSuccess?: (member: Member) => void;
  onCancel?: () => void;
}

const MemberForm: React.FC<MemberFormProps> = ({
  member,
  onSuccess,
  onCancel,
}) => {
  const { t } = useTranslation();
  const isEditing = !!member;
  
  const [formData, setFormData] = useState<MemberFormData>({
    name: '',
    position: '',
    email: '',
    phone: '',
    biography: '',
    photoUrl: '',
    hierarchyLevel: 0,
    tenureStartDate: '',
    tenureEndDate: '',
    isActive: true,
    isProminent: false,
  });
  
  const [errors, setErrors] = useState<MemberFormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [photoFile, setPhotoFile] = useState<File | null>(null);

  useEffect(() => {
    if (member) {
      setFormData({
        name: member.name || '',
        position: member.position || '',
        email: member.email || '',
        phone: member.phone || '',
        biography: member.biography || '',
        photoUrl: member.photoUrl || '',
        hierarchyLevel: member.hierarchyLevel || 0,
        tenureStartDate: member.tenureStartDate || '',
        tenureEndDate: member.tenureEndDate || '',
        isActive: member.isActive ?? true,
        isProminent: member.isProminent ?? false,
      });
    }
  }, [member]);

  const validateForm = (): boolean => {
    const newErrors: MemberFormErrors = {};

    if (!formData.name.trim()) {
      newErrors.name = t('validation.required', { field: t('members.name') });
    }

    if (!formData.position.trim()) {
      newErrors.position = t('validation.required', { field: t('members.position') });
    }

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = t('validation.invalidEmail');
    }

    if (formData.hierarchyLevel < 0 || formData.hierarchyLevel > 10) {
      newErrors.hierarchyLevel = t('validation.range', { field: t('members.hierarchy'), min: 0, max: 10 });
    }

    if (formData.tenureStartDate && formData.tenureEndDate) {
      const startDate = new Date(formData.tenureStartDate);
      const endDate = new Date(formData.tenureEndDate);
      
      if (endDate <= startDate) {
        newErrors.tenureEndDate = t('validation.endDateAfterStart');
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (field: keyof MemberFormData, value: string | number | boolean) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Clear error when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setPhotoFile(file);
      
      // Create preview URL
      const reader = new FileReader();
      reader.onload = (e) => {
        setFormData(prev => ({ ...prev, photoUrl: e.target?.result as string }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      setIsSubmitting(true);
      
      let savedMember: Member;
      
      if (isEditing && member) {
        const updateData: UpdateMemberRequest = {
          id: member.id,
          name: formData.name,
          position: formData.position,
          email: formData.email || undefined,
          phone: formData.phone || undefined,
          biography: formData.biography || undefined,
          hierarchyLevel: formData.hierarchyLevel,
          tenureStartDate: formData.tenureStartDate || undefined,
          tenureEndDate: formData.tenureEndDate || undefined,
          isActive: formData.isActive,
          isProminent: formData.isProminent,
        };
        
        savedMember = await membersService.updateMember(updateData);
      } else {
        const createData: CreateMemberRequest = {
          name: formData.name,
          position: formData.position,
          email: formData.email || undefined,
          phone: formData.phone || undefined,
          biography: formData.biography || undefined,
          hierarchyLevel: formData.hierarchyLevel,
          tenureStartDate: formData.tenureStartDate || new Date().toISOString().split('T')[0],
          tenureEndDate: formData.tenureEndDate || undefined,
          isActive: formData.isActive,
          isProminent: formData.isProminent,
        };
        
        savedMember = await membersService.createMember(createData);
      }
      
      // Upload photo if provided
      if (photoFile && savedMember.id) {
        try {
          const photoUrl = await membersService.uploadMemberPhoto(savedMember.id, photoFile);
          savedMember.photoUrl = photoUrl;
        } catch (photoError) {
          console.warn('Failed to upload photo:', photoError);
          // Continue without photo - don't fail the entire operation
        }
      }
      
      onSuccess?.(savedMember);
      
    } catch (err) {
      setErrors({
        submit: err instanceof Error ? err.message : t('members.form.submitError')
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const positionOptions = [
    'President',
    'Vice President',
    'Secretary',
    'Joint Secretary',
    'Treasurer',
    'Executive Member',
    'Technical Committee Member',
    'Tournament Director',
    'Coaching Director',
    'Media Relations Officer',
  ];

  return (
    <FormContainer>
      <form onSubmit={handleSubmit}>
        <FormGrid>
          <FormGroup>
            <Label htmlFor="name">
              {t('members.name')} *
            </Label>
            <Input
              id="name"
              type="text"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              placeholder={t('members.form.namePlaceholder')}
              hasError={!!errors.name}
              disabled={isSubmitting}
            />
            {errors.name && <ErrorMessage>{errors.name}</ErrorMessage>}
          </FormGroup>

          <FormGroup>
            <Label htmlFor="position">
              {t('members.position')} *
            </Label>
            <Select
              id="position"
              value={formData.position}
              onChange={(e) => handleInputChange('position', e.target.value)}
              hasError={!!errors.position}
              disabled={isSubmitting}
            >
              <option value="">{t('members.form.selectPosition')}</option>
              {positionOptions.map(position => (
                <option key={position} value={position}>
                  {position}
                </option>
              ))}
            </Select>
            {errors.position && <ErrorMessage>{errors.position}</ErrorMessage>}
          </FormGroup>

          <FormGroup>
            <Label htmlFor="email">
              {t('members.email')}
            </Label>
            <Input
              id="email"
              type="email"
              value={formData.email}
              onChange={(e) => handleInputChange('email', e.target.value)}
              placeholder={t('members.form.emailPlaceholder')}
              hasError={!!errors.email}
              disabled={isSubmitting}
            />
            {errors.email && <ErrorMessage>{errors.email}</ErrorMessage>}
          </FormGroup>

          <FormGroup>
            <Label htmlFor="phone">
              {t('members.phone')}
            </Label>
            <Input
              id="phone"
              type="tel"
              value={formData.phone}
              onChange={(e) => handleInputChange('phone', e.target.value)}
              placeholder={t('members.form.phonePlaceholder')}
              disabled={isSubmitting}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="hierarchyLevel">
              {t('members.hierarchy')}
            </Label>
            <Select
              id="hierarchyLevel"
              value={formData.hierarchyLevel}
              onChange={(e) => handleInputChange('hierarchyLevel', parseInt(e.target.value))}
              hasError={!!errors.hierarchyLevel}
              disabled={isSubmitting}
            >
              <option value={0}>{t('members.hierarchy.executive')}</option>
              <option value={1}>{t('members.hierarchy.senior')}</option>
              <option value={2}>{t('members.hierarchy.general')}</option>
            </Select>
            {errors.hierarchyLevel && <ErrorMessage>{errors.hierarchyLevel}</ErrorMessage>}
          </FormGroup>

          <FormGroup>
            <Label htmlFor="tenureStartDate">
              {t('members.tenureStart')}
            </Label>
            <Input
              id="tenureStartDate"
              type="date"
              value={formData.tenureStartDate}
              onChange={(e) => handleInputChange('tenureStartDate', e.target.value)}
              disabled={isSubmitting}
            />
          </FormGroup>

          <FormGroup>
            <Label htmlFor="tenureEndDate">
              {t('members.tenureEnd')}
            </Label>
            <Input
              id="tenureEndDate"
              type="date"
              value={formData.tenureEndDate}
              onChange={(e) => handleInputChange('tenureEndDate', e.target.value)}
              hasError={!!errors.tenureEndDate}
              disabled={isSubmitting}
            />
            {errors.tenureEndDate && <ErrorMessage>{errors.tenureEndDate}</ErrorMessage>}
          </FormGroup>

          <FormGroup fullWidth>
            <Label htmlFor="biography">
              {t('members.biography')}
            </Label>
            <TextArea
              id="biography"
              value={formData.biography}
              onChange={(e) => handleInputChange('biography', e.target.value)}
              placeholder={t('members.form.biographyPlaceholder')}
              disabled={isSubmitting}
            />
          </FormGroup>

          <FormGroup fullWidth>
            <Label>{t('members.photo')}</Label>
            <PhotoUploadSection>
              {formData.photoUrl && (
                <PhotoPreview>
                  <img src={formData.photoUrl} alt="Preview" />
                </PhotoPreview>
              )}
              <input
                type="file"
                accept="image/*"
                onChange={handlePhotoChange}
                disabled={isSubmitting}
                style={{ marginBottom: '8px' }}
              />
              <p style={{ margin: 0, fontSize: '14px', color: '#6b7280' }}>
                {t('members.form.photoHint')}
              </p>
            </PhotoUploadSection>
          </FormGroup>

          <FormGroup>
            <CheckboxGroup>
              <Checkbox
                id="isActive"
                type="checkbox"
                checked={formData.isActive}
                onChange={(e) => handleInputChange('isActive', e.target.checked)}
                disabled={isSubmitting}
              />
              <CheckboxLabel htmlFor="isActive">
                {t('members.isActive')}
              </CheckboxLabel>
            </CheckboxGroup>
          </FormGroup>

          <FormGroup>
            <CheckboxGroup>
              <Checkbox
                id="isProminent"
                type="checkbox"
                checked={formData.isProminent}
                onChange={(e) => handleInputChange('isProminent', e.target.checked)}
                disabled={isSubmitting}
              />
              <CheckboxLabel htmlFor="isProminent">
                {t('members.isProminent')}
              </CheckboxLabel>
            </CheckboxGroup>
          </FormGroup>
        </FormGrid>

        {errors.submit && (
          <ErrorMessage style={{ marginTop: '16px', textAlign: 'center' }}>
            {errors.submit}
          </ErrorMessage>
        )}
      </form>

      <Modal.Footer>
        <Button
          variant="secondary"
          onClick={onCancel}
          disabled={isSubmitting}
        >
          {t('common.cancel')}
        </Button>
        <Button
          type="submit"
          disabled={isSubmitting}
        >
          {isSubmitting 
            ? t('common.saving') 
            : isEditing 
              ? t('common.update') 
              : t('common.create')
          }
        </Button>
      </Modal.Footer>
    </FormContainer>
  );
};

export default MemberForm;